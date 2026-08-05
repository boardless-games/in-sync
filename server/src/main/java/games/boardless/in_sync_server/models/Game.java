package games.boardless.in_sync_server.models;

import games.boardless.in_sync_server.constants.GameStatus;
import games.boardless.in_sync_server.constants.MessageTopic;
import games.boardless.in_sync_server.constants.ScheduleType;
import games.boardless.in_sync_server.constants.SongDuration;
import games.boardless.in_sync_server.dtos.ErrorDto;
import games.boardless.in_sync_server.dtos.MessageDto;
import games.boardless.in_sync_server.dtos.PerformanceDto;
import games.boardless.in_sync_server.dtos.ScheduleDto;
import games.boardless.in_sync_server.dtos.SongSettingsDto;
import games.boardless.in_sync_server.exceptions.BadRequestException;
import games.boardless.in_sync_server.exceptions.ServiceUnavailableException;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

public class Game {
  public static final int GAME_CODE_LENGTH = 6;
  public static final int GAME_CODE_MIN = Math.powExact(10, GAME_CODE_LENGTH - 1);
  public static final int GAME_CODE_MAX = Math.powExact(10, GAME_CODE_LENGTH);
  public static final int MAX_NUM_PLAYERS = 20;
  public static final int MIN_PLAYER_NAME_LENGTH = 2;
  public static final int MAX_PLAYER_NAME_LENGTH = 15;
  public static final int NUM_LOBBY_RHYTHMS = 5;

  private static final Logger logger = LoggerFactory.getLogger(Game.class);
  private static final Random rand = new Random();
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String generateGameCode() {
    return String.valueOf(rand.nextInt(GAME_CODE_MAX - GAME_CODE_MIN) + GAME_CODE_MIN);
  }

  public static Optional<String> validateGameCode(final String gameCode) {
    if (gameCode == null) {
      return Optional.of("Game code must be defined.");
    } else if (gameCode.isBlank()) {
      return Optional.of("Game code cannot be empty.");
    } else if (gameCode.length() != Game.GAME_CODE_LENGTH
        || !Pattern.matches("^[0-9]+$", gameCode)) {
      return Optional.of(String.format("Game code must be %d digits.", Game.GAME_CODE_LENGTH));
    }
    return Optional.empty();
  }

  public static Optional<String> validatePlayerName(final String name) {
    if (name == null) {
      return Optional.of("Name must be defined.");
    } else if (name.isBlank()) {
      return Optional.of("Name cannot be empty.");
    } else if (name.length() != name.trim().length()) {
      return Optional.of("Name cannot contain extra whitespace.");
    } else if (name.length() < Game.MIN_PLAYER_NAME_LENGTH) {
      return Optional.of(
          String.format("Name must be at least %d characters long.", Game.MIN_PLAYER_NAME_LENGTH));
    } else if (name.length() > Game.MAX_PLAYER_NAME_LENGTH) {
      return Optional.of(
          String.format("Name cannot be longer than %d characters.", Game.MAX_PLAYER_NAME_LENGTH));
    } else if (!Pattern.matches("^[a-zA-Z0-9]+$", name)) {
      return Optional.of("Name can only contain letters and numbers.");
    }
    return Optional.empty();
  }

  private final String gameCode;
  private final int playerWaitTime;
  private final int scheduleOffsetTime;
  private GameStatus status;
  private final int lobbyRhythm;
  private Song song = null;
  private long schedule = 0l;
  private final Map<String, Player> players;
  private final Map<String, Performance> performances;
  private Runnable onReadyTask = null;

  public Game(final String gameCode, final int playerWaitTime, final int scheduleOffsetTime) {
    this.gameCode = gameCode;
    this.playerWaitTime = playerWaitTime;
    this.scheduleOffsetTime = scheduleOffsetTime;
    this.status = GameStatus.LOBBY;
    this.lobbyRhythm = rand.nextInt(NUM_LOBBY_RHYTHMS);
    this.players = new ConcurrentHashMap<>();
    this.performances = new ConcurrentHashMap<>();
  }

  public String getGameCode() {
    return this.gameCode;
  }

  public GameStatus getStatus() {
    return this.status;
  }

  public int getLobbyRhythm() {
    return this.lobbyRhythm;
  }

  public SongDuration getSongDuration() throws ServiceUnavailableException {
    if (this.song == null) {
      throw new ServiceUnavailableException(
          String.format("Game %s does not have a song.", this.gameCode));
    }
    return this.song.getDuration();
  }

  public boolean isScheduled() {
    return this.schedule != 0l;
  }

  public long getSchedule() {
    return this.schedule;
  }

  public String[] getPlayers() {
    return this.players.values().stream()
        .sorted()
        .map(player -> player.getName())
        .toArray(String[]::new);
  }

  public int numPlayers() {
    return this.players.size();
  }

  private void clearOnReadyTask() {
    this.onReadyTask = null;
  }

  private void clearSchedule() {
    this.schedule = 0l;
    logger.info("The schedule for game {} was cleared.", this.gameCode);
  }

  public void cancelSchedule(final String reason) {
    this.clearSchedule();
    this.clearOnReadyTask();
    this.status = GameStatus.IN_GAME;
    this.messagePlayers(MessageTopic.CANCEL_SCHEDULE, new ErrorDto(reason));
    logger.info("The schedule for game {} was cleared because \"{}\".", this.gameCode, reason);
  }

  public boolean hasConnectedPlayers() {
    return this.players.values().stream().anyMatch(player -> player.isConnected());
  }

  public boolean arePlayersReady() {
    return this.players.values().stream().allMatch(player -> player.isReady());
  }

  public void addPlayer(final String name) throws ServiceUnavailableException, BadRequestException {
    synchronized (this.players) {
      if (this.status != GameStatus.LOBBY) {
        throw new ServiceUnavailableException(
            String.format("Game %s has already started.", this.gameCode));
      }
      if (this.players.size() >= MAX_NUM_PLAYERS) {
        throw new ServiceUnavailableException(
            String.format("Game %s is at max capacity.", this.gameCode));
      }
      if (this.players.get(name) != null) {
        throw new BadRequestException(String.format("The name %s is already taken.", name));
      }
      final int nextPosition =
          this.players.values().stream().mapToInt(player -> player.getPosition()).max().orElse(0)
              + 1;
      this.players.put(name, new Player(name, nextPosition));
      logger.info("{} was added to game {}.", name, this.gameCode);
    }
  }

  public void removePlayer(final String name) throws BadRequestException {
    synchronized (this.players) {
      if (this.players.remove(name) == null) {
        throw new BadRequestException(
            String.format("%s is not a player in game %s.", name, this.gameCode));
      }
      logger.info("{} was removed from game {}.", name, this.gameCode);
    }
  }

  public void connect(final String playerName, final WebSocketSession session)
      throws BadRequestException {
    final Player player = this.players.get(playerName);
    if (player == null) {
      throw new BadRequestException(
          String.format("%s is not a player in game %s.", playerName, this.gameCode));
    }
    player.connect(session);

    logger.info("{} connected to game {}.", playerName, this.gameCode);

    if (this.status == GameStatus.LOBBY) {
      this.messagePlayers(MessageTopic.LOBBY, this.getPlayers());
    }
  }

  public boolean isConnected(final String playerName) {
    final Player player = this.players.get(playerName);
    return player == null ? false : player.isConnected();
  }

  public void disconnect(final String playerName) throws BadRequestException {
    final Player player = this.players.get(playerName);
    if (player == null) {
      throw new BadRequestException(
          String.format("%s is not a player in game %s.", playerName, this.gameCode));
    }
    player.disconnect();

    logger.info("{} disconnected from game {}.", playerName, this.gameCode);

    if (this.status == GameStatus.LOBBY) {
      this.removePlayer(playerName);
      this.messagePlayers(MessageTopic.LOBBY, this.getPlayers());
    }
  }

  public void disconnectAll() {
    for (final Player player : this.players.values()) {
      if (player.isConnected()) {
        player.disconnect();
        logger.info("{} was disconnected from game {}.", player.getName(), this.gameCode);
      }
    }
  }

  public synchronized void pingPlayers() {
    logger.info("Pinging {} in game {}.", this.getPlayers(), this.gameCode);
    for (final Player player : this.players.values()) {
      player.ping();
    }
  }

  public synchronized void messagePlayers(final MessageTopic topic, final Object data) {
    logger.info("Messaging {} to {} in game {}.", topic, this.getPlayers(), this.gameCode);
    final TextMessage textMessage =
        new TextMessage(Game.objectMapper.writeValueAsString((new MessageDto(topic, data))));
    for (final Player player : this.players.values()) {
      player.message(textMessage);
    }
  }

  public void setAllNotReady() {
    for (final Player player : this.players.values()) {
      player.setReady(false);
    }
    logger.info("All players are now not ready in game {}.", this.gameCode);
  }

  public synchronized void setReady(final String playerName)
      throws BadRequestException, ServiceUnavailableException {
    final Player player = this.players.get(playerName);
    if (player == null) {
      throw new BadRequestException(
          String.format("%s is not a player in game %s.", playerName, this.gameCode));
    }
    if (player.isReady()) {
      throw new ServiceUnavailableException(
          String.format("%s is already ready in game %s.", player.getName(), this.gameCode));
    }
    player.setReady(true);
    logger.info("{} is now ready in game {}.", playerName, this.gameCode);
    if (this.onReadyTask != null && this.arePlayersReady()) {
      this.onReadyTask.run();
      this.clearOnReadyTask();
    }
  }

  public synchronized void prepare(
      final SongSettingsDto songSettings, final DeferredResult<ResponseEntity<Void>> deferredResult)
      throws ServiceUnavailableException {
    if (this.status != GameStatus.LOBBY) {
      throw new ServiceUnavailableException(
          String.format("Cannot prepare game %s right now.", this.gameCode));
    }

    logger.info("Preparing game {}.", this.gameCode);
    this.status = GameStatus.PREPARING;

    this.onReadyTask =
        () -> {
          try {
            this.start(songSettings);
            deferredResult.setResult(ResponseEntity.status(HttpStatus.OK).build());
          } catch (Exception e) {
            try {
              this.toLobby();
            } catch (Exception unexpectedException) {
              logger.error(
                  "Unexpected error after failing to start game {} and attempting to return to the lobby.",
                  this.gameCode,
                  unexpectedException);
            } finally {
              deferredResult.setErrorResult(e);
            }
          }
        };

    this.pingPlayers();
  }

  public synchronized void start(final SongSettingsDto songSettings)
      throws ServiceUnavailableException, BadRequestException {
    if (this.status != GameStatus.PREPARING) {
      throw new ServiceUnavailableException(
          String.format("Cannot start game %s right now.", this.gameCode));
    }
    if (!this.arePlayersReady()) {
      throw new ServiceUnavailableException(
          String.format("Players in game %s are not ready.", this.gameCode));
    }

    logger.info("Starting game {}.", this.gameCode);
    this.status = GameStatus.IN_GAME;

    this.song = new Song(songSettings, this.getPlayers());

    this.messagePlayers(MessageTopic.SONG, this.song);
  }

  public synchronized void toLobby() throws ServiceUnavailableException {
    if (this.status != GameStatus.PREPARING && this.status != GameStatus.IN_GAME) {
      throw new ServiceUnavailableException(
          String.format("Cannot return to the lobby in game %s right now.", this.gameCode));
    }

    logger.info("Returning to lobby in game {}.", this.gameCode);
    this.status = GameStatus.LOBBY;
    this.clearOnReadyTask();

    this.players.values().removeIf((player) -> !player.isConnected());

    this.messagePlayers(MessageTopic.LOBBY, this.getPlayers());
  }

  public synchronized void schedule(
      final ScheduleType scheduleType, final DeferredResult<ResponseEntity<Void>> deferredResult)
      throws ServiceUnavailableException {
    if (this.status != GameStatus.IN_GAME) {
      throw new ServiceUnavailableException(
          String.format("Cannot schedule in game %s right now.", this.gameCode));
    }

    this.setAllNotReady();

    this.schedule = System.currentTimeMillis() + this.playerWaitTime + this.scheduleOffsetTime;

    if (scheduleType == ScheduleType.PLAYBACK) {
      this.status = GameStatus.PLAYINGBACK;
    } else {
      this.status = GameStatus.PERFORMING;
      this.performances.clear();
    }

    this.onReadyTask =
        () -> {
          deferredResult.setResult(ResponseEntity.status(HttpStatus.OK).build());
        };

    this.messagePlayers(MessageTopic.SCHEDULE, new ScheduleDto(scheduleType, this.schedule));
  }

  public void acknowledgeSchedule(final String playerName, final long schedule)
      throws BadRequestException, ServiceUnavailableException {
    if (!this.isScheduled()) {
      throw new ServiceUnavailableException(
          String.format("There is no schedule in game %s.", this.gameCode));
    }

    if (this.schedule != schedule) {
      throw new BadRequestException(
          String.format("%d is not the correct schedule in game %s.", schedule, this.gameCode));
    }

    this.setReady(playerName);
    logger.info("{} acknowledged the schedule in game {}.", playerName, this.gameCode);
  }

  public synchronized void submitPerformance(final PerformanceDto performance)
      throws ServiceUnavailableException, BadRequestException {
    if (this.status != GameStatus.PERFORMING) {
      throw new ServiceUnavailableException(
          String.format("Game %s is not in a performance.", this.gameCode));
    }

    if (this.schedule != performance.schedule()) {
      throw new BadRequestException(
          String.format("Invalid performanc schedule for game %s.", this.gameCode));
    }

    final Player player = this.players.get(performance.playerName());
    if (player == null) {
      throw new BadRequestException(
          String.format("%s is not a player in game %s.", performance.playerName(), this.gameCode));
    }

    if (this.performances.containsKey(player.getName())) {
      throw new ServiceUnavailableException(
          String.format("%s already submitted a performance.", player.getName()));
    }

    this.performances.put(
        player.getName(), new Performance(player.getName(), this.song, performance));
    logger.info("{} submitted their performance in game {}.", player.getName(), this.gameCode);

    if (this.performances.size() == this.players.size()) {
      this.endPerformance();
    }
  }

  public synchronized void endPlayback() {
    if (this.status != GameStatus.PLAYINGBACK) {
      return;
    }
    this.status = GameStatus.IN_GAME;
    this.clearSchedule();
    logger.info("A playback has ended in game {}.", this.gameCode);
  }

  public synchronized void endPerformance() {
    if (this.status != GameStatus.PERFORMING) {
      return;
    }
    this.messagePlayers(MessageTopic.PERFORMANCE_RESULTS, this.performances.values().toArray());
    this.status = GameStatus.IN_GAME;
    this.clearSchedule();
    logger.info("A performance has ended in game {}.", this.gameCode);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((gameCode == null) ? 0 : gameCode.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Game other = (Game) obj;
    if (gameCode == null) {
      if (other.gameCode != null) return false;
    } else if (!gameCode.equals(other.gameCode)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Game [gameCode=" + gameCode + ", status=" + status + ", players=" + players + "]";
  }
}
