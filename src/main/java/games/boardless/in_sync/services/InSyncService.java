package games.boardless.in_sync.services;

import static games.boardless.in_sync.constants.Constants.GAME_AUTO_DELETE_TIME;
import static games.boardless.in_sync.constants.Constants.MAX_NUM_GAMES;
import static games.boardless.in_sync.constants.Constants.PLAYER_AUTO_REMOVE_TIME;
import static games.boardless.in_sync.constants.Constants.WAIT_PLAYER_READY_TIME;

import games.boardless.in_sync.constants.ScheduleType;
import games.boardless.in_sync.dtos.AcknowledgeScheduleDto;
import games.boardless.in_sync.dtos.GameCodeDto;
import games.boardless.in_sync.dtos.GameSettingsDto;
import games.boardless.in_sync.dtos.PlayerNameDto;
import games.boardless.in_sync.exceptions.BadRequestException;
import games.boardless.in_sync.exceptions.ServiceUnavailableException;
import games.boardless.in_sync.models.Game;
import games.boardless.in_sync.models.Game.GameStatus;
import games.boardless.in_sync.utils.InputValidation;
import games.boardless.in_sync.utils.ToString;
import java.io.IOException;
import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class InSyncService {
  private static final Logger logger = LoggerFactory.getLogger(InSyncService.class);
  private final TaskScheduler taskScheduler;
  private final Clock clock;
  final Map<String, Game> games = new ConcurrentHashMap<>();

  @Autowired
  public InSyncService(final TaskScheduler taskScheduler, final Clock clock) {
    this.taskScheduler = taskScheduler;
    this.clock = clock;
  }

  public synchronized ResponseEntity<GameCodeDto> newGame()
      throws BadRequestException, ServiceUnavailableException {
    // Is the server already at the max capacity?
    if (this.games.size() >= MAX_NUM_GAMES) {
      throw new ServiceUnavailableException(
          "The server is at max capacity. Please try again later.");
    }

    // Generate and validate the new game code.
    String gameCode;
    do {
      gameCode = Game.generateGameCode();
    } while (this.games.containsKey(gameCode));

    // Create and add the new game and player.
    final Game newGame = new Game(gameCode);
    this.games.put(gameCode, newGame);

    // Create auto delete timer.
    this.taskScheduler.schedule(
        () -> {
          autoDeleteGame(newGame.getGameCode());
        },
        this.clock.instant().plusMillis(GAME_AUTO_DELETE_TIME));

    logger.info("Created game {}.", gameCode);

    return ResponseEntity.status(HttpStatus.CREATED).body(new GameCodeDto(gameCode));
  }

  public ResponseEntity<Void> newPlayer(final String gameCode, final PlayerNameDto name)
      throws BadRequestException, ServiceUnavailableException {
    // Validate the game code.
    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    // Validate the name.
    if (name == null) {
      throw new BadRequestException("Name must be provided.");
    }

    final Optional<String> nameValidation = InputValidation.validateName(name.playerName());
    if (nameValidation.isPresent()) {
      throw new BadRequestException(nameValidation.get());
    }

    // Get the game.
    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    // Add the player
    game.addPlayer(name.playerName());

    // Create auto remove timer.
    this.taskScheduler.schedule(
        () -> {
          autoRemovePlayer(game.getGameCode(), name.playerName());
        },
        this.clock.instant().plusMillis(PLAYER_AUTO_REMOVE_TIME));

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  public DeferredResult<ResponseEntity<Void>> startGame(
      final String gameCode, final GameSettingsDto gameSettings)
      throws BadRequestException, ServiceUnavailableException {
    // Validate the game code.
    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    // Get the game.
    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    game.initialize();

    final DeferredResult<ResponseEntity<Void>> deferredResult =
        new DeferredResult<>(
            WAIT_PLAYER_READY_TIME + 5_000L,
            new ServiceUnavailableException(
                String.format("Request timed out while starting game %s.", game.getGameCode())));

    // Create auto start timer.
    this.taskScheduler.schedule(
        () -> {
          try {
            autoStartGame(gameCode, gameSettings);
            deferredResult.setResult(ResponseEntity.ok().build());
          } catch (Exception e) {
            deferredResult.setErrorResult(e);
          }
        },
        this.clock.instant().plusMillis(WAIT_PLAYER_READY_TIME));

    return deferredResult;
  }

  public DeferredResult<ResponseEntity<Void>> schedule(
      final String gameCode, final ScheduleType type)
      throws BadRequestException, ServiceUnavailableException {

    // Validate the game code.
    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    // Get the game.
    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    game.schedule(type);

    final DeferredResult<ResponseEntity<Void>> deferredResult =
        new DeferredResult<>(
            WAIT_PLAYER_READY_TIME + 5_000L,
            new ServiceUnavailableException(
                String.format(
                    "Request timed out while scheduling for game %s.", game.getGameCode())));

    // Create timer.
    this.taskScheduler.schedule(
        () -> {
          try {
            autoVerifyScheduleAcknowledgement(gameCode);
            deferredResult.setResult(ResponseEntity.ok().build());
          } catch (Exception e) {
            deferredResult.setErrorResult(e);
          }
        },
        this.clock.instant().plusMillis(WAIT_PLAYER_READY_TIME));

    return deferredResult;
  }

  public ResponseEntity<Void> acknowledgePerformanceSchedule(
      final String gameCode, final AcknowledgeScheduleDto ack) throws BadRequestException {
    // Validate the game code.
    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    // Validate the acknowledgment
    if (ack == null) {
      throw new BadRequestException("The acknowledgment must be provided.");
    }

    final Optional<String> nameValidation = InputValidation.validateName(ack.playerName());
    if (nameValidation.isPresent()) {
      throw new BadRequestException(nameValidation.get());
    }

    // Get the game.
    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    game.acknowledgePerformanceSchedule(ack.playerName(), ack.schedule());

    return ResponseEntity.ok().build();
  }

  public void connect(final WebSocketSession session) {
    // Validate session
    if (session == null) {
      return;
    }

    // Get the query params.
    MultiValueMap<String, String> queryParams =
        UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams();

    try {
      // Get and validate the game code.
      final String gameCode = queryParams.get("gameCode").getFirst();
      final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      // Get the game.
      final Game game = this.games.get(gameCode);
      if (game == null) {
        throw new BadRequestException(String.format("Game %s not found.", gameCode));
      }

      // Get and validate the name.
      final String name = queryParams.get("name").getFirst();
      final Optional<String> nameValidation = InputValidation.validateName(name);
      if (nameValidation.isPresent()) {
        throw new BadRequestException(nameValidation.get());
      }

      // Set the player's session.
      game.connect(name, session);
    } catch (NullPointerException | BadRequestException badDataException) {
      logger.info(
          "Closing {} after connection established due to bad data.",
          ToString.toString(session),
          badDataException);
      try {
        session.close(CloseStatus.BAD_DATA);
      } catch (IOException ioException) {
        logger.error(
            "Failed to close {} after connection established due to bad data.",
            ToString.toString(session),
            ioException);
      }
    } catch (Exception e) {
      logger.error("Unexpected error while connecting.", e);
    }
  }

  public void disconnect(final WebSocketSession session) {
    // Validate session
    if (session == null) {
      return;
    }

    // Get the query params.
    MultiValueMap<String, String> queryParams =
        UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams();

    try {
      // Get and validate the game code.
      final String gameCode = queryParams.get("gameCode").getFirst();
      final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      // Get the game.
      final Game game = this.games.get(gameCode);
      if (game == null) {
        return;
      }

      // Get and validate the name.
      final String name = queryParams.get("name").getFirst();
      final Optional<String> nameValidation = InputValidation.validateName(name);
      if (nameValidation.isPresent()) {
        throw new BadRequestException(nameValidation.get());
      }

      game.disconnect(name);

      if (game.getStatus() == GameStatus.LOBBY) {
        game.removePlayer(name);
        // Lobbies can have players that have joined but not yet connected.
        if (game.numPlayers() == 0) {
          this.deleteGame(game.getGameCode());
        }
      } else if (!game.hasConnectedPlayers()) {
        this.deleteGame(game.getGameCode());
      }
    } catch (NullPointerException | BadRequestException badDataException) {
      logger.info(
          "Failed to disconnect from {} due to bad data.",
          ToString.toString(session),
          badDataException);
    } catch (Exception e) {
      logger.error("Unexpected error while disconnecting.", e);
    }
  }

  public void handlePongMessage(final WebSocketSession session) {
    // Validate session
    if (session == null) {
      return;
    }

    // Get the query params.
    MultiValueMap<String, String> queryParams =
        UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams();

    try {
      // Get and validate the game code.
      final String gameCode = queryParams.get("gameCode").getFirst();
      final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      // Get the game.
      final Game game = this.games.get(gameCode);
      if (game == null) {
        throw new BadRequestException(String.format("Game %s not found.", gameCode));
      }

      // Get and validate the name.
      final String name = queryParams.get("name").getFirst();
      final Optional<String> nameValidation = InputValidation.validateName(name);
      if (nameValidation.isPresent()) {
        throw new BadRequestException(nameValidation.get());
      }

      // Set ready.
      game.setReady(name);
    } catch (NullPointerException | BadRequestException e) {
      logger.info(
          "Failed to handle pong message from {} due to bad data.", ToString.toString(session), e);
    } catch (Exception e) {
      logger.error(
          "Unexpected error while handling pong message from {}.", ToString.toString(session), e);
    }
  }

  void autoDeleteGame(final String gameCode) {
    // Validate gameCode
    if (gameCode == null) {
      return;
    }

    // Get the game.
    final Game game = this.games.get(gameCode);
    if (game == null || game.hasConnectedPlayers()) {
      return;
    }

    this.deleteGame(gameCode);
  }

  public void deleteGame(final String gameCode) {
    // Validate gameCode
    if (gameCode == null) {
      return;
    }

    // Delete the game.
    final Game deletedGame = this.games.remove(gameCode);
    if (deletedGame == null) {
      logger.error("Failed to delete game {}.", gameCode);
      return;
    }

    logger.info("Deleted game {}.", gameCode);
  }

  void autoRemovePlayer(final String gameCode, final String name) {
    // Validate gameCode and name
    if (gameCode == null || name == null) {
      return;
    }

    // Find the game.
    final Game game = this.games.get(gameCode);
    if (game == null) {
      return;
    }

    // Remove the player if they are not connected.
    if (!game.isConnected(name)) {
      try {
        game.removePlayer(name);
      } catch (BadRequestException e) {
        logger.error("Failed to auto remove {} from game {}.", name, gameCode, e);
      }
    }
  }

  void autoStartGame(final String gameCode, final GameSettingsDto gameSettings)
      throws BadRequestException, ServiceUnavailableException {

    if (gameCode == null || gameSettings == null) {
      throw new BadRequestException("Game code and game settings must be provided.");
    }

    // Get the game.
    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s was deleted while starting.", gameCode));
    }

    // Start the game.
    game.start(gameSettings);
  }

  void autoVerifyScheduleAcknowledgement(final String gameCode)
      throws BadRequestException, ServiceUnavailableException {

    if (gameCode == null) {
      throw new BadRequestException("Game code must be provided");
    }

    // Get the game.
    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(
          String.format("Game %s was deleted while scheduling.", gameCode));
    }

    if (!game.isPerformanceScheduled()) {
      throw new ServiceUnavailableException(String.format("Failed to schedule for game %s."));
    }

    if (!game.arePlayersReady()) {
      game.clearPerformanceSchedule();
      throw new ServiceUnavailableException(
          String.format("Players in game %s have not acknowledged the schedule."));
    }
  }
}
