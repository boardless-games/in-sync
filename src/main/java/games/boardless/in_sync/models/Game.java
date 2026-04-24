package games.boardless.in_sync.models;

import static games.boardless.in_sync.constants.Constants.GAME_CODE_LENGTH;
import static games.boardless.in_sync.constants.Constants.GAME_CODE_MAX;
import static games.boardless.in_sync.constants.Constants.GAME_CODE_MIN;
import static games.boardless.in_sync.constants.Constants.MAX_NUM_PLAYERS;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.web.socket.WebSocketSession;

import games.boardless.in_sync.exceptions.BadRequestException;
import games.boardless.in_sync.exceptions.ServiceUnavailableException;

public class Game {
  private static final Random rand = new Random();

  private final String gameCode;
  private final Map<String, Player> players;

  public Game(final String gameCode) {
    this.gameCode = gameCode;
    this.players = new ConcurrentHashMap<>();
  }

  // Getters and setters
  public String getGameCode() {
    return this.gameCode;
  }

  public String[] getPlayers() {
    return this.players.keySet().toArray(new String[0]);
  }

  @Override
  public String toString() {
    return "Game [gameCode=" + gameCode + ", players=" + players + "]";
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

  // Service methods
  public static String generateGameCode() {
    return String.valueOf(rand.nextInt(GAME_CODE_MAX - GAME_CODE_MIN) + GAME_CODE_MIN);
  }

  public synchronized void addPlayer(final String name) throws ServiceUnavailableException, BadRequestException {
    if (this.players.size() >= MAX_NUM_PLAYERS) {
      throw new ServiceUnavailableException("The game is at max capacity.");
    }
    if (this.players.get(name) != null) {
      throw new BadRequestException("That name is already taken.");
    }
    this.players.put(name, new Player(name));
  }

  public boolean hasConnectedPlayers() {
    return this.players.values().stream().anyMatch((final Player player) -> player.isConnected());
  }

  public boolean setPlayerSession(final String name, final WebSocketSession session) {
    final Player player = this.players.get(name);
    if (player == null) {
      return false;
    }
    player.setSession(session);
    return true;
  }
}
