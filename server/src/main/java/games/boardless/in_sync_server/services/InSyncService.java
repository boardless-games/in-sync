package games.boardless.in_sync_server.services;

import static games.boardless.in_sync_server.constants.Constants.DEV_PROFILE;

import games.boardless.in_sync_server.constants.GameStatus;
import games.boardless.in_sync_server.constants.ScheduleType;
import games.boardless.in_sync_server.dtos.AcknowledgeScheduleDto;
import games.boardless.in_sync_server.dtos.GameCodeDto;
import games.boardless.in_sync_server.dtos.PerformanceDto;
import games.boardless.in_sync_server.dtos.PlayerNameDto;
import games.boardless.in_sync_server.dtos.ScheduleDto;
import games.boardless.in_sync_server.dtos.SongSettingsDto;
import games.boardless.in_sync_server.exceptions.BadRequestException;
import games.boardless.in_sync_server.exceptions.NotFoundException;
import games.boardless.in_sync_server.exceptions.ServiceUnavailableException;
import games.boardless.in_sync_server.handlers.InSyncWebSocketHandler;
import games.boardless.in_sync_server.models.Game;
import java.io.IOException;
import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
  public final int GAME_AUTO_DELETE_TIME = 60_000;

  @Value("${insync.max.games}")
  public int MAX_NUM_GAMES;

  @Value("${insync.player.wait.time}")
  public int PLAYER_WAIT_TIME;

  @Value("${insync.premade.games}:")
  public String PREMADE_GAMES;

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

    if (!this.PREMADE_GAMES.isBlank()) {
      for (final String gameCode : this.PREMADE_GAMES.split(",")) {
        final Game newGame = new Game(gameCode);
        this.games.put(gameCode, newGame);
      }
    }
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

  public synchronized ResponseEntity<GameCodeDto> getGame(final String gameCode)
      throws BadRequestException {
    final Optional<String> gameCodeValidation = Game.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    return ResponseEntity.ok(new GameCodeDto(game.getGameCode()));
  }

  public ResponseEntity<Void> newPlayer(final String gameCode, final PlayerNameDto name)
      throws BadRequestException, ServiceUnavailableException {
    final Optional<String> gameCodeValidation = Game.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    if (name == null) {
      throw new BadRequestException("Name must be provided.");
    }

    final Optional<String> nameValidation = Game.validatePlayerName(name.playerName());
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
        this.clock.instant().plusMillis(PLAYER_WAIT_TIME));

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  public DeferredResult<ResponseEntity<Void>> start(
      final String gameCode, final SongSettingsDto songSettings)
      throws BadRequestException, ServiceUnavailableException {
    final Optional<String> gameCodeValidation = Game.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    final DeferredResult<ResponseEntity<Void>> deferredResult = new DeferredResult<>();

    game.prepare(songSettings, deferredResult);

    this.taskScheduler.schedule(
        () -> {
          try {
            autoVerifyStart(gameCode);
          } catch (Exception e) {
            deferredResult.setErrorResult(e);
          }
        },
        this.clock.instant().plusMillis(PLAYER_WAIT_TIME));

    return deferredResult;
  }

  public ResponseEntity<Void> toLobby(final String gameCode)
      throws BadRequestException, ServiceUnavailableException {
    final Optional<String> gameCodeValidation = Game.validateGameCode(gameCode);
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
    if (!this.environment.matchesProfiles(DEV_PROFILE)) {
      throw new ServiceUnavailableException("This endpoint is currently not available.");
    }

    final Optional<String> gameCodeValidation = Game.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    if (!game.isScheduled()) {
      throw new ServiceUnavailableException(
          String.format("No schedule found for game %s.", game.getGameCode()));
    }

    final GameStatus gameStatus = game.getStatus();
    ScheduleType scheduleType;
    if (gameStatus == GameStatus.PLAYINGBACK) {
      scheduleType = ScheduleType.PLAYBACK;
    } else if (gameStatus == GameStatus.PERFORMING) {
      scheduleType = ScheduleType.PERFORMANCE;
    } else {
      logger.error("Unexpected error when getting the schedule type for game {}.", gameCode);
      throw new ServiceUnavailableException(
          String.format("No valid schedule found for game %s.", game.getGameCode()));
    }

    return ResponseEntity.ok(new ScheduleDto(scheduleType, game.getSchedule()));
  }

  public DeferredResult<ResponseEntity<Void>> schedule(
      final String gameCode, final ScheduleType scheduleType)
      throws BadRequestException, ServiceUnavailableException {

    final Optional<String> gameCodeValidation = Game.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s not found.", gameCode));
    }

    final DeferredResult<ResponseEntity<Void>> deferredResult = new DeferredResult<>();

    game.schedule(scheduleType, deferredResult);

    this.taskScheduler.schedule(
        () -> {
          try {
            autoVerifyScheduleAcknowledgement(gameCode);
          } catch (Exception e) {
            deferredResult.setErrorResult(e);
          }
        },
        this.clock.instant().plusMillis(PLAYER_WAIT_TIME));

    this.taskScheduler.schedule(
        () -> {
          try {
            autoClearSchedule(gameCode, scheduleType);
          } catch (Exception e) {
            logger.error("Failed to auto clear schedule.", e);
          }
        },
        this.clock
            .instant()
            .plusMillis(
                Game.SCHEDULE_OFFSET_TIME
                    + game.getSongDuration().getDuration()
                    + PLAYER_WAIT_TIME));

    return deferredResult;
  }

  public ResponseEntity<Void> acknowledgeSchedule(
      final String gameCode, final AcknowledgeScheduleDto ack)
      throws BadRequestException, ServiceUnavailableException {
    final Optional<String> gameCodeValidation = Game.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    if (ack == null) {
      throw new BadRequestException("The acknowledgment must be provided.");
    }

    final Optional<String> nameValidation = Game.validatePlayerName(ack.playerName());
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
    final Optional<String> gameCodeValidation = Game.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    if (performance == null) {
      throw new BadRequestException("The performance must be provided.");
    }

    final Optional<String> nameValidation = Game.validatePlayerName(performance.playerName());
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
      final Optional<String> gameCodeValidation = Game.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      final Game game = this.games.get(gameCode);
      if (game == null) {
        throw new BadRequestException(String.format("Game %s not found.", gameCode));
      }

      final String playerName = queryParams.get("playerName").getFirst();
      final Optional<String> nameValidation = Game.validatePlayerName(playerName);
      if (nameValidation.isPresent()) {
        throw new BadRequestException(nameValidation.get());
      }

      game.connect(playerName, session);
    } catch (NullPointerException | BadRequestException badDataException) {
      logger.info(
          "Closing {} after connection established due to bad data.",
          InSyncWebSocketHandler.getWebSocketSessionString(session),
          badDataException);
      try {
        session.close(CloseStatus.BAD_DATA);
      } catch (IOException ioException) {
        logger.error(
            "Failed to close {} after connection established due to bad data.",
            InSyncWebSocketHandler.getWebSocketSessionString(session),
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
      final Optional<String> gameCodeValidation = Game.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      final Game game = this.games.get(gameCode);
      if (game == null) {
        return;
      }

      final String playerName = queryParams.get("playerName").getFirst();
      final Optional<String> nameValidation = Game.validatePlayerName(playerName);
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
          InSyncWebSocketHandler.getWebSocketSessionString(session),
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
      final Optional<String> gameCodeValidation = Game.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      final Game game = this.games.get(gameCode);
      if (game == null) {
        throw new BadRequestException(String.format("Game %s not found.", gameCode));
      }

      final String playerName = queryParams.get("playerName").getFirst();
      final Optional<String> nameValidation = Game.validatePlayerName(playerName);
      if (nameValidation.isPresent()) {
        throw new BadRequestException(nameValidation.get());
      }

      game.setReady(playerName);
    } catch (NullPointerException | BadRequestException e) {
      logger.info(
          "Failed to handle pong message from {} due to bad data.",
          InSyncWebSocketHandler.getWebSocketSessionString(session),
          e);
    } catch (Exception e) {
      logger.error(
          "Unexpected error while handling pong message from {}.",
          InSyncWebSocketHandler.getWebSocketSessionString(session),
          e);
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

  void autoVerifyStart(final String gameCode)
      throws BadRequestException, ServiceUnavailableException {

    if (gameCode == null) {
      throw new BadRequestException("Game code must be provided.");
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("Game %s was deleted while starting.", gameCode));
    }

    if (game.getStatus() == GameStatus.PREPARING) {
      game.toLobby();
      throw new ServiceUnavailableException(String.format("Failed to start game %s.", gameCode));
    }
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

    try {
      if (!game.isScheduled()) {
        throw new ServiceUnavailableException(
            String.format("Failed to schedule in game %s.", gameCode));
      }

      if (!game.arePlayersReady()) {
        throw new ServiceUnavailableException(
            String.format("Players in game %s have not acknowledged the schedule.", gameCode));
      }
    } catch (Exception e) {
      game.cancelSchedule(e.getMessage());
      throw e;
    }
  }

  void autoClearSchedule(final String gameCode, final ScheduleType scheduleType)
      throws BadRequestException {
    if (gameCode == null) {
      throw new BadRequestException("Game code must be provided.");
    }

    final Game game = this.games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(
          String.format("Game %s was deleted while scheduling.", gameCode));
    }

    if (scheduleType == ScheduleType.PLAYBACK) {
      game.endPlayback();
    } else if (scheduleType == ScheduleType.PERFORMANCE) {
      game.endPerformance();
    }
  }
}
