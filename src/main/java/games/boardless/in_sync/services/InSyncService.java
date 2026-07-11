package games.boardless.in_sync.services;

import static games.boardless.in_sync.constants.Constants.GAME_AUTO_DELETE_TIME;
import static games.boardless.in_sync.constants.Constants.MAX_NUM_GAMES;
import static games.boardless.in_sync.constants.Constants.PLAYER_AUTO_REMOVE_TIME;
import static games.boardless.in_sync.constants.Constants.SCHEDULE_OFFSET;
import static games.boardless.in_sync.constants.Constants.WAIT_PLAYER_READY_TIME;

import games.boardless.in_sync.constants.GameStatus;
import games.boardless.in_sync.constants.ScheduleType;
import games.boardless.in_sync.dtos.AcknowledgeScheduleDto;
import games.boardless.in_sync.dtos.GameCodeDto;
import games.boardless.in_sync.dtos.PerformanceDto;
import games.boardless.in_sync.dtos.PlayerNameDto;
import games.boardless.in_sync.dtos.ScheduleDto;
import games.boardless.in_sync.dtos.SongSettingsDto;
import games.boardless.in_sync.exceptions.BadRequestException;
import games.boardless.in_sync.exceptions.NotFoundException;
import games.boardless.in_sync.exceptions.ServiceUnavailableException;
import games.boardless.in_sync.models.Game;
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
import org.springframework.core.env.Environment;
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
  private final Environment environment;
  private final Clock clock;
  final Map<String, Game> games = new ConcurrentHashMap<>();

  @Autowired
  public InSyncService(
      final TaskScheduler taskScheduler, final Environment environment, final Clock clock) {
    this.taskScheduler = taskScheduler;
    this.environment = environment;
    this.clock = clock;
  }

  public synchronized ResponseEntity<GameCodeDto> newGame()
      throws BadRequestException, ServiceUnavailableException {
    if (this.games.size() >= MAX_NUM_GAMES) {
      throw new ServiceUnavailableException(
          "The server is at max capacity. Please try again later.");
    }

    String gameCode;
    do {
      gameCode = Game.generateGameCode();
    } while (this.games.containsKey(gameCode));

    final Game newGame = new Game(gameCode);
    this.games.put(gameCode, newGame);

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
    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    if (name == null) {
      throw new BadRequestException("Name must be provided.");
    }

    final Optional<String> nameValidation = InputValidation.validatePlayerName(name.playerName());
    if (nameValidation.isPresent()) {
      throw new BadRequestException(nameValidation.get());
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    game.addPlayer(name.playerName());

    this.taskScheduler.schedule(
        () -> {
          autoRemovePlayer(game.getGameCode(), name.playerName());
        },
        this.clock.instant().plusMillis(PLAYER_AUTO_REMOVE_TIME));

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  public DeferredResult<ResponseEntity<Void>> newSong(
      final String gameCode, final SongSettingsDto gameSettings)
      throws BadRequestException, ServiceUnavailableException {
    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    game.initializeNewSong();

    final DeferredResult<ResponseEntity<Void>> deferredResult =
        new DeferredResult<>(
            WAIT_PLAYER_READY_TIME + 5_000L,
            new ServiceUnavailableException(
                String.format(
                    "Request timed out while creating new song in game %s.", game.getGameCode())));

    this.taskScheduler.schedule(
        () -> {
          try {
            autoNewSong(gameCode, gameSettings);
            deferredResult.setResult(ResponseEntity.status(HttpStatus.CREATED).build());
          } catch (Exception e) {
            deferredResult.setErrorResult(e);
          }
        },
        this.clock.instant().plusMillis(WAIT_PLAYER_READY_TIME));

    return deferredResult;
  }

  public ResponseEntity<Void> toLobby(final String gameCode)
      throws BadRequestException, ServiceUnavailableException {
    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    game.toLobby();

    return ResponseEntity.ok().build();
  }

  public ResponseEntity<ScheduleDto> getSchedule(final String gameCode)
      throws NotFoundException, BadRequestException, ServiceUnavailableException {
    if (!this.environment.matchesProfiles("dev")) {
      throw new ServiceUnavailableException("This endpoint is currently not available.");
    }

    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    if (!game.isScheduled()) {
      throw new NotFoundException(
          String.format("No schedule found for game %s.", game.getGameCode()));
    }

    return ResponseEntity.ok(
        new ScheduleDto(
            game.getStatus() == GameStatus.PERFORMING
                ? ScheduleType.PERFORMANCE
                : ScheduleType.PLAYBACK,
            game.getSchedule()));
  }

  public ResponseEntity<Void> schedule(final String gameCode, final ScheduleType scheduleType)
      throws BadRequestException, ServiceUnavailableException {

    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    final long schedule = game.schedule(scheduleType);

    this.taskScheduler.schedule(
        () -> {
          try {
            autoVerifyScheduleAcknowledgement(gameCode);
          } catch (Exception e) {
            game.cancelSchedule(e.getMessage());
          }
        },
        this.clock.instant().plusMillis(WAIT_PLAYER_READY_TIME));

    this.taskScheduler.schedule(
        () -> {
          try {
            autoClearSchedule(gameCode, scheduleType, schedule);
          } catch (Exception e) {
            logger.error("Failed to auto clear schedule.", e);
          }
        },
        this.clock.instant().plusMillis(SCHEDULE_OFFSET));

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  public ResponseEntity<Void> acknowledgeSchedule(
      final String gameCode, final AcknowledgeScheduleDto ack)
      throws BadRequestException, ServiceUnavailableException {
    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    if (ack == null) {
      throw new BadRequestException("The acknowledgment must be provided.");
    }

    final Optional<String> nameValidation = InputValidation.validatePlayerName(ack.playerName());
    if (nameValidation.isPresent()) {
      throw new BadRequestException(nameValidation.get());
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    game.acknowledgeSchedule(ack.playerName(), ack.schedule());

    return ResponseEntity.ok().build();
  }

  public ResponseEntity<Void> newPerformance(
      final String gameCode, final PerformanceDto performance)
      throws BadRequestException, ServiceUnavailableException {
    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    if (performance == null) {
      throw new BadRequestException("The performance must be provided.");
    }

    final Optional<String> nameValidation =
        InputValidation.validatePlayerName(performance.playerName());
    if (nameValidation.isPresent()) {
      throw new BadRequestException(nameValidation.get());
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    game.submitPerformance(performance);

    return ResponseEntity.ok().build();
  }

  public void connect(final WebSocketSession session) {
    if (session == null) {
      return;
    }

    MultiValueMap<String, String> queryParams =
        UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams();

    try {
      final String gameCode = queryParams.get("gameCode").getFirst();
      final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      final Game game = this.games.get(gameCode);
      if (game == null) {
        throw new BadRequestException(String.format("Game %s not found.", gameCode));
      }

      final String playerName = queryParams.get("playerName").getFirst();
      final Optional<String> nameValidation = InputValidation.validatePlayerName(playerName);
      if (nameValidation.isPresent()) {
        throw new BadRequestException(nameValidation.get());
      }

      game.connect(playerName, session);
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
    if (session == null) {
      return;
    }

    MultiValueMap<String, String> queryParams =
        UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams();

    try {
      final String gameCode = queryParams.get("gameCode").getFirst();
      final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      final Game game = this.games.get(gameCode);
      if (game == null) {
        return;
      }

      final String playerName = queryParams.get("playerName").getFirst();
      final Optional<String> nameValidation = InputValidation.validatePlayerName(playerName);
      if (nameValidation.isPresent()) {
        throw new BadRequestException(nameValidation.get());
      }

      game.disconnect(playerName);

      final GameStatus gameStatus = game.getStatus();
      if ((gameStatus == GameStatus.LOBBY && game.numPlayers() == 0)
          || (gameStatus != GameStatus.LOBBY && !game.hasConnectedPlayers())) {
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
    if (session == null) {
      return;
    }

    MultiValueMap<String, String> queryParams =
        UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams();

    try {
      final String gameCode = queryParams.get("gameCode").getFirst();
      final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      final Game game = this.games.get(gameCode);
      if (game == null) {
        throw new BadRequestException(String.format("Game %s not found.", gameCode));
      }

      final String playerName = queryParams.get("playerName").getFirst();
      final Optional<String> nameValidation = InputValidation.validatePlayerName(playerName);
      if (nameValidation.isPresent()) {
        throw new BadRequestException(nameValidation.get());
      }

      game.setReady(playerName);
    } catch (NullPointerException | BadRequestException e) {
      logger.info(
          "Failed to handle pong message from {} due to bad data.", ToString.toString(session), e);
    } catch (Exception e) {
      logger.error(
          "Unexpected error while handling pong message from {}.", ToString.toString(session), e);
    }
  }

  public void deleteGame(final String gameCode) {
    if (gameCode == null) {
      return;
    }

    final Game deletedGame = this.games.remove(gameCode);
    if (deletedGame == null) {
      logger.error("Failed to delete game {}.", gameCode);
      return;
    }

    logger.info("Deleted game {}.", gameCode);
  }

  void autoDeleteGame(final String gameCode) {
    if (gameCode == null) {
      return;
    }

    final Game game = this.games.get(gameCode);
    if (game == null || game.hasConnectedPlayers()) {
      return;
    }

    this.deleteGame(gameCode);
  }

  void autoRemovePlayer(final String gameCode, final String name) {
    if (gameCode == null || name == null) {
      return;
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      return;
    }

    if (!game.isConnected(name)) {
      try {
        game.removePlayer(name);
      } catch (BadRequestException e) {
        logger.error("Failed to auto remove {} from game {}.", name, gameCode, e);
      }
    }
  }

  void autoNewSong(final String gameCode, final SongSettingsDto songSettings)
      throws BadRequestException, ServiceUnavailableException {

    if (gameCode == null || songSettings == null) {
      throw new BadRequestException("Game code and game settings must be provided.");
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s was deleted while starting.", gameCode));
    }

    game.newSong(songSettings);
  }

  void autoVerifyScheduleAcknowledgement(final String gameCode)
      throws BadRequestException, ServiceUnavailableException {

    if (gameCode == null) {
      throw new BadRequestException("Game code must be provided.");
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new ServiceUnavailableException(
          String.format("Game %s was deleted while scheduling.", gameCode));
    }

    if (!game.isScheduled()) {
      throw new ServiceUnavailableException(
          String.format("Failed to schedule in game %s.", gameCode));
    }

    if (!game.arePlayersReady()) {
      throw new ServiceUnavailableException(
          String.format("Players in game %s have not acknowledged the schedule.", gameCode));
    }
  }

  void autoClearSchedule(
      final String gameCode, final ScheduleType scheduleType, final long schedule)
      throws BadRequestException {
    if (gameCode == null) {
      throw new BadRequestException("Game code must be provided.");
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(
          String.format("Game %s was deleted while scheduling.", gameCode));
    }

    if (scheduleType == ScheduleType.PLAYBACK && game.getSchedule() == schedule) {
      game.clearSchedule();
    }
  }
}
