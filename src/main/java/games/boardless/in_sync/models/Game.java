package games.boardless.in_sync.models;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;

public class Game {
  private static final Random rand = new Random();
  // Exclusive
  private static final int MAX_GAME_CODE = 1_000_000;
  // Inclusive
  private static final int MIN_GAME_CODE = 100_000;

  private final String gameCode;
  private final CopyOnWriteArraySet<Player> players;

  public Game(final String gameCode) {
    this.gameCode = gameCode;
    this.players = new CopyOnWriteArraySet<>();
  }

  // Getters and setters
  public String getGameCode() {
    return this.gameCode;
  }

  public String[] getPlayers() {
    return this.players.toArray(new String[0]);
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
    return String.valueOf(rand.nextInt(MAX_GAME_CODE - MIN_GAME_CODE) + MIN_GAME_CODE);
  }

  public boolean addPlayer(final String name) {
    return this.players.add(new Player(name));
  }

  public boolean hasConnectedPlayers() {
    return this.players.stream().anyMatch((final Player player) -> player.isConnected());
  }
}
