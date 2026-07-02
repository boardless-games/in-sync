package games.boardless.in_sync.models;

import static games.boardless.in_sync.constants.Constants.GAME_CODE_MAX;
import static games.boardless.in_sync.constants.Constants.GAME_CODE_MIN;
import static games.boardless.in_sync.constants.Constants.MAX_NUM_PLAYERS;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import games.boardless.in_sync.constants.GameType;
import games.boardless.in_sync.dtos.GameSettingsDto;
import games.boardless.in_sync.exceptions.BadRequestException;
import games.boardless.in_sync.exceptions.ServiceUnavailableException;

public class Game {
  public enum GameStatus {
    LOBBY,
    INITIALIZING,
    IN_GAME
  }

  private static final Logger logger = LoggerFactory.getLogger(Game.class);
  private static final Random rand = new Random();

  private final String gameCode;
  private GameStatus status;
  private GameType type;
  private final ReentrantLock playerLock;
  private final Map<String, Player> players;

  public Game(final String gameCode) {
    this.gameCode = gameCode;
    this.status = GameStatus.LOBBY;
    this.playerLock = new ReentrantLock(true);
    this.players = new ConcurrentHashMap<>();
  }

  // Getters and setters
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
    return this.players.keySet().toArray(new String[0]);
  }

  public int numPlayers() {
    return this.players.size();
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
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Game other = (Game) obj;
    if (gameCode == null) {
      if (other.gameCode != null)
        return false;
    } else if (!gameCode.equals(other.gameCode))
      return false;
    return true;
  }

  public static String generateGameCode() {
    return String.valueOf(rand.nextInt(GAME_CODE_MAX - GAME_CODE_MIN) + GAME_CODE_MIN);
  }

  public boolean hasConnectedPlayers() {
    return this.players.values().stream().anyMatch((final Player player) -> player.isConnected());
  }

  public boolean playersAreReady() {
    return this.players.values().stream().allMatch((final Player player) -> player.isReady());
  }

  public void addPlayer(final String name) throws ServiceUnavailableException, BadRequestException {
    this.playerLock.lock();
    try {
      if (this.status != GameStatus.LOBBY) {
        throw new ServiceUnavailableException(String.format("Game %s has already started.", this.gameCode));
      }
      if (this.players.size() >= MAX_NUM_PLAYERS) {
        throw new ServiceUnavailableException(String.format("Game %s is at max capacity.", this.gameCode));
      }
      if (this.players.get(name) != null) {
        throw new BadRequestException(String.format("The name %s is already taken.", name));
      }
      this.players.put(name, new Player(name));
      logger.info("{} was added to game {}.", name, this.gameCode);
    } finally {
      this.playerLock.unlock();
    }
  }

  public void removePlayer(final String name) throws BadRequestException {
    this.playerLock.lock();
    try {
      if (this.players.remove(name) == null) {
        throw new BadRequestException(String.format("%s is not a player in game %s.", name, this.gameCode));
      }
      logger.info("{} was removed from game {}.", name, this.gameCode);
    } finally {
      this.playerLock.unlock();
    }
  }

  public void connect(final String playerName, final WebSocketSession session) throws BadRequestException {
    final Player player = this.players.get(playerName);
    if (player == null) {
      throw new BadRequestException(String.format("%s is not a player in game %s.", playerName, this.gameCode));
    }
    player.connect(session);
    logger.info("{} connected to game {}.", playerName, this.gameCode);
  }

  public boolean isConnected(final String playerName) {
    final Player player = this.players.get(playerName);
    return player == null ? false : player.isConnected();
  }

  public void disconnect(final String playerName) throws BadRequestException {
    final Player player = this.players.get(playerName);
    if (player == null) {
      throw new BadRequestException(String.format("%s is not a player in game %s.", playerName, this.gameCode));
    }
    player.disconnect();
    logger.info("{} disconnected from game {}.", playerName, this.gameCode);
  }

  public void disconnectAll() {
    for (final Player player : this.players.values()) {
      if (player.isConnected()) {
        player.disconnect();
        logger.info("{} was disconnected from game {}.", player.getName(), this.gameCode);
      }
    }
  }

  public synchronized void pingPlayers() throws ServiceUnavailableException {
    logger.info("Pinging {} in game {}.", this.getPlayers(), this.gameCode);
    for (final Player player : this.players.values()) {
      player.ping();
    }
  }

  public void setReady(final String playerName) throws BadRequestException {
    final Player player = this.players.get(playerName);
    if (player == null) {
      throw new BadRequestException(String.format("%s is not a player in game %s.", playerName, this.gameCode));
    }
    player.setReady();
    logger.info("{} is ready in game {}.", playerName, this.gameCode);
  }

  public synchronized void initialize() throws ServiceUnavailableException {
    if (this.status != GameStatus.LOBBY) {
      throw new ServiceUnavailableException(String.format("Game %s has already started.", this.gameCode));
    }

    this.status = GameStatus.INITIALIZING;

    this.pingPlayers();
  }

  public synchronized void start(final GameSettingsDto gameSettings) throws ServiceUnavailableException {
    if (this.status == GameStatus.LOBBY) {
      throw new ServiceUnavailableException(String.format("Game %s has not been initialized.", this.gameCode));
    }
    if (this.status == GameStatus.IN_GAME) {
      throw new ServiceUnavailableException(String.format("Game %s has already started.", this.gameCode));
    }
    if (!this.playersAreReady()) {
      throw new ServiceUnavailableException(String.format("Players in game %s are not ready.", gameCode));
    }

    logger.info("Starting game {}.", gameCode);
    this.type = gameSettings.gameType();
    this.status = GameStatus.IN_GAME;
  }
}
