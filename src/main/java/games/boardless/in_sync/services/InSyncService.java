package games.boardless.in_sync.services;

import static games.boardless.in_sync.constants.Constants.GAME_AUTO_DELETE_TIME;
import static games.boardless.in_sync.constants.Constants.MAX_NUM_GAMES;
import static games.boardless.in_sync.constants.Constants.PLAYER_AUTO_REMOVE_TIME;
import static games.boardless.in_sync.constants.Constants.WAIT_PLAYER_READY_TIME;

import java.io.IOException;
import java.time.Instant;
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

import games.boardless.in_sync.dtos.GameCodeDto;
import games.boardless.in_sync.dtos.NameDto;
import games.boardless.in_sync.exceptions.BadRequestException;
import games.boardless.in_sync.exceptions.ServiceUnavailableException;
import games.boardless.in_sync.models.Game;
import games.boardless.in_sync.models.Game.GameStatus;
import games.boardless.in_sync.utils.InputValidation;
import games.boardless.in_sync.utils.ToString;

@Service
public class InSyncService {
  private static final Logger logger = LoggerFactory.getLogger(InSyncService.class);
  private static final Map<String, Game> games = new ConcurrentHashMap<>();
  private final TaskScheduler taskScheduler;

  @Autowired
  public InSyncService(final TaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  public synchronized ResponseEntity<GameCodeDto> newGame()
      throws BadRequestException, ServiceUnavailableException {
    // Is the server already at the max capacity?
    if (games.size() >= MAX_NUM_GAMES) {
      throw new ServiceUnavailableException("The server is at max capacity. Please try again later.");
    }

    // Generate and validate the new game code.
    String gameCode;
    do {
      gameCode = Game.generateGameCode();
    } while (games.containsKey(gameCode));

    // Create and add the new game and player.
    final Game newGame = new Game(gameCode);
    games.put(gameCode, newGame);

    // Create auto delete timer.
    this.taskScheduler.schedule(() -> {
      autoDeleteGame(newGame.getGameCode());
    }, Instant.now().plusMillis(GAME_AUTO_DELETE_TIME));

    logger.info("Created game {}.", gameCode);

    return ResponseEntity.status(HttpStatus.CREATED).body(new GameCodeDto(gameCode));
  }

  public ResponseEntity<Void> newPlayer(final String gameCode, final NameDto nameDto)
      throws BadRequestException, ServiceUnavailableException {
    // Validate the game code.
    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    // Validate the name.
    final Optional<String> nameValidation = InputValidation.validateName(nameDto.name());
    if (nameValidation.isPresent()) {
      throw new BadRequestException(nameValidation.get());
    }

    // Get the game.
    final Game game = games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    // Add the player
    game.addPlayer(nameDto.name());

    // Create auto remove timer.
    this.taskScheduler.schedule(() -> {
      autoRemovePlayer(game.getGameCode(), nameDto.name());
    }, Instant.now().plusMillis(PLAYER_AUTO_REMOVE_TIME));

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  public DeferredResult<ResponseEntity<Void>> startGame(final String gameCode)
      throws BadRequestException, ServiceUnavailableException {
    // Validate the game code.
    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    // Get the game.
    final Game game = games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    game.initialize();

    final DeferredResult<ResponseEntity<Void>> deferredResult = new DeferredResult<>(10000L);

    // Create auto start timer.
    this.taskScheduler.schedule(() -> {
      try {
        autoStartGame(gameCode);
        deferredResult.setResult(ResponseEntity.ok().build());
      } catch (Exception e) {
        deferredResult.setErrorResult(e);
      }
    }, Instant.now().plusMillis(WAIT_PLAYER_READY_TIME));

    return deferredResult;
  }

  public void connect(final WebSocketSession session) {
    // Get the query params.
    MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(session.getUri()).build()
        .getQueryParams();

    try {
      // Get and validate the game code.
      final String gameCode = queryParams.get("gameCode").getFirst();
      final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      // Get the game.
      final Game game = games.get(gameCode);
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
      logger.info("Closing {} after connection established due to bad data.",
          ToString.toString(session), badDataException);
      try {
        session.close(CloseStatus.BAD_DATA);
      } catch (IOException ioException) {
        logger.error("Failed to close {} after connection established due to bad data.",
            ToString.toString(session), ioException);
      }
    } catch (Exception e) {
      logger.error("Unexpected error while connecting.", e);
    }
  }

  public void disconnect(final WebSocketSession session) {
    // Get the query params.
    MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(session.getUri()).build()
        .getQueryParams();

    try {
      // Get and validate the game code.
      final String gameCode = queryParams.get("gameCode").getFirst();
      final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      // Get the game.
      final Game game = games.get(gameCode);
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
      logger.info("Failed to disconnect from {} due to bad data.",
          ToString.toString(session), badDataException);
    } catch (Exception e) {
      logger.error("Unexpected error while disconnecting.", e);
    }
  }

  public void handlePongMessage(final WebSocketSession session) {
    // Get the query params.
    MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(session.getUri()).build()
        .getQueryParams();

    try {
      // Get and validate the game code.
      final String gameCode = queryParams.get("gameCode").getFirst();
      final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      // Get the game.
      final Game game = games.get(gameCode);
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
      logger.info("Failed to handle pong message from {} due to bad data.", ToString.toString(session), e);
    } catch (Exception e) {
      logger.error("Unexpected error while handling pong message from {}.", ToString.toString(session), e);
    }
  }

  private void autoDeleteGame(final String gameCode) {
    // Get the game.
    final Game game = games.get(gameCode);
    if (game == null || game.hasConnectedPlayers()) {
      return;
    }

    this.deleteGame(gameCode);
  }

  public void deleteGame(final String gameCode) {
    // Delete the game.
    final Game deletedGame = games.remove(gameCode);
    if (deletedGame == null) {
      logger.error("Failed to delete game {}.", gameCode);
      return;
    }

    logger.info("Deleted game {}.", gameCode);
  }

  private void autoRemovePlayer(final String gameCode, final String name) {
    // Find the game.
    final Game game = games.get(gameCode);
    if (game == null) {
      return;
    }

    // Delete the player if they are not connected.
    if (!game.isConnected(name)) {
      try {
        game.removePlayer(name);
      } catch (BadRequestException e) {
        logger.error("Failed to auto remove {} from game {}.", name, gameCode, e);
      }
    }
  }

  private void autoStartGame(final String gameCode) throws BadRequestException, ServiceUnavailableException {
    // Get the game.
    final Game game = games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s was deleted while starting.", gameCode));
    }

    // Start the game.
    game.start();
  }
}
