package games.boardless.in_sync.models;

import static games.boardless.in_sync.constants.Constants.GAME_CODE_MAX;
import static games.boardless.in_sync.constants.Constants.GAME_CODE_MIN;
import static games.boardless.in_sync.constants.Constants.MAX_NUM_PLAYERS;
import static games.boardless.in_sync.constants.Constants.SCHEDULE_OFFSET;

import games.boardless.in_sync.constants.GameDifficulty;
import games.boardless.in_sync.constants.GameStatus;
import games.boardless.in_sync.constants.GameType;
import games.boardless.in_sync.constants.MessageTopic;
import games.boardless.in_sync.constants.ScheduleType;
import games.boardless.in_sync.dtos.GameSettingsDto;
import games.boardless.in_sync.dtos.ScheduleDto;
import games.boardless.in_sync.exceptions.BadRequestException;
import games.boardless.in_sync.exceptions.ServiceUnavailableException;
import games.boardless.in_sync.utils.ToTextMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class Game {
  private static final Logger logger = LoggerFactory.getLogger(Game.class);
  private static final Random rand = new Random();

  private final String gameCode;
  private GameStatus status;
  private GameType type;
  private GameDifficulty difficulty;
  private Song song = null;
  private long schedule = 0l;
  private final Map<String, Player> players;

  public Game(final String gameCode) {
    this.gameCode = gameCode;
    this.status = GameStatus.LOBBY;
    this.players = new ConcurrentHashMap<>();
  }

  public String getGameCode() {
    return this.gameCode;
  }

  public GameStatus getStatus() {
    return this.status;
  }

  public GameType getType() {
    return this.type;
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

  public boolean isScheduled() {
    return this.schedule != 0l;
  }

  public long getSchedule() {
    return this.schedule;
  }

  public void clearSchedule() {
    this.schedule = 0l;
    logger.info("The schedule for game {} was cleared.", this.gameCode);
  }

  public void cancelSchedule() {
    this.clearSchedule();
    this.status = GameStatus.IN_GAME;
    this.messagePlayers(MessageTopic.CANCEL_SCHEDULE, null);
    logger.info("The schedule for game {} was cleared because it was canceled.", this.gameCode);
  }

  @Override
  public String toString() {
    return "Game [gameCode=" + gameCode + ", status=" + this.status + ", players=" + players + "]";
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

  public static String generateGameCode() {
    return String.valueOf(rand.nextInt(GAME_CODE_MAX - GAME_CODE_MIN) + GAME_CODE_MIN);
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
    final TextMessage textMessage = ToTextMessage.toTextMessage(new Message(topic, data));
    for (final Player player : this.players.values()) {
      player.message(textMessage);
    }
  }

  public void setReady(final String playerName) throws BadRequestException {
    final Player player = this.players.get(playerName);
    if (player == null) {
      throw new BadRequestException(
          String.format("%s is not a player in game %s.", playerName, this.gameCode));
    }
    player.setReady(true);
    logger.info("{} is ready in game {}.", playerName, this.gameCode);
  }

  public synchronized void initialize() throws ServiceUnavailableException {
    if (this.status != GameStatus.LOBBY) {
      throw new ServiceUnavailableException(
          String.format("Game %s has already started.", this.gameCode));
    }

    this.status = GameStatus.INITIALIZING;

    this.pingPlayers();
  }

  public synchronized void start(final GameSettingsDto gameSettings)
      throws ServiceUnavailableException {
    if (this.status == GameStatus.LOBBY) {
      throw new ServiceUnavailableException(
          String.format("Game %s has not been initialized.", this.gameCode));
    }
    if (this.status == GameStatus.IN_GAME) {
      throw new ServiceUnavailableException(
          String.format("Game %s has already started.", this.gameCode));
    }
    if (!this.arePlayersReady()) {
      throw new ServiceUnavailableException(
          String.format("Players in game %s are not ready.", gameCode));
    }

    logger.info("Starting game {}.", gameCode);
    this.type = gameSettings.gameType();
    this.difficulty = gameSettings.gameDifficulty();
    this.status = GameStatus.IN_GAME;

    final ArrayList<String> playerNames = new ArrayList<>(Arrays.asList(this.getPlayers()));
    this.song = new Song(this.type, this.difficulty, playerNames);

    this.messagePlayers(MessageTopic.SONG, this.song);
  }

  public synchronized void quit() throws ServiceUnavailableException {
    if (this.status != GameStatus.IN_GAME) {
      throw new ServiceUnavailableException(
          String.format("Game %s cannot be quit right now.", this.gameCode));
    }

    logger.info("Quitting game {}.", gameCode);
    this.status = GameStatus.LOBBY;
    this.clearSchedule();
    this.song = null;

    this.messagePlayers(MessageTopic.QUIT, null);
  }

  public synchronized long schedule(final ScheduleType scheduleType)
      throws ServiceUnavailableException {
    if (this.status != GameStatus.IN_GAME) {
      throw new ServiceUnavailableException(
          String.format("Game %s has not been started.", this.gameCode));
    }

    if (this.isScheduled()) {
      throw new ServiceUnavailableException(
          String.format("Game %s has already been scheduled.", this.gameCode));
    }

    for (final Player player : this.players.values()) {
      player.setReady(false);
    }

    this.schedule = System.currentTimeMillis() + SCHEDULE_OFFSET;

    if (scheduleType == ScheduleType.PERFORMANCE) {
      this.status = GameStatus.PERFORMING;
    }

    this.messagePlayers(MessageTopic.SCHEDULE, new ScheduleDto(scheduleType, this.schedule));

    return this.schedule;
  }

  public void acknowledgeSchedule(final String playerName, final long schedule)
      throws BadRequestException, ServiceUnavailableException {
    final Player player = this.players.get(playerName);
    if (player == null) {
      throw new BadRequestException(
          String.format("%s is not a player in game %s.", playerName, this.gameCode));
    }

    if (!this.isScheduled()) {
      throw new ServiceUnavailableException(
          String.format("There is no schedule in game %s.", this.gameCode));
    }

    if (this.schedule != schedule) {
      throw new BadRequestException(
          String.format("%d is not the correct schedule in game %s.", schedule, this.gameCode));
    }

    if (player.isReady()) {
      throw new ServiceUnavailableException(
          String.format(
              "%s already acknowledged the schedule for game %s.",
              player.getName(), this.gameCode));
    }

    player.setReady(true);
    logger.info("{} acknowledged the schedule in game {}.", playerName, this.gameCode);
  }
}
