package games.boardless.in_sync.constants;

public class Constants {
  public static final String APP_NAME = "In Sync";
  public static final String APP_DESCRIPTION = "A multiplayer rhythm game.";
  public static final int GAME_CODE_LENGTH = 6;
  public static final int GAME_CODE_MIN = Math.powExact(10, GAME_CODE_LENGTH - 1);
  public static final int GAME_CODE_MAX = Math.powExact(10, GAME_CODE_LENGTH);
  public static final int MAX_NUM_GAMES = 1;
  public static final long GAME_AUTO_DELETE_TIME = 30_000L;
  public static final long PLAYER_AUTO_REMOVE_TIME = 10_000L;
  public static final long WAIT_PLAYER_READY_TIME = 2_000L;
  public static final int MIN_PLAYER_NAME_LENGTH = 3;
  public static final int MAX_PLAYER_NAME_LENGTH = 15;
  public static final int MAX_NUM_PLAYERS = 20;
  public static final String[] ALLOWED_ORIGINS = {
      "http://localhost:4200"
  };
  public static final int MILLIS_PER_MINUTE = 60_000;
}
