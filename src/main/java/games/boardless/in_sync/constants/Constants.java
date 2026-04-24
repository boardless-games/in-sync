package games.boardless.in_sync.constants;

public class Constants {
  public static final String APP_NAME = "In Sync";
  public static final String APP_DESCRIPTION = "A multiplayer rhythm game.";
  public static final int GAME_CODE_LENGTH = 6;
  public static final int GAME_CODE_MIN = Math.powExact(10, GAME_CODE_LENGTH - 1);
  public static final int GAME_CODE_MAX = Math.powExact(10, GAME_CODE_LENGTH);
  public static final int MIN_PLAYER_NAME_LENGTH = 3;
  public static final int MAX_PLAYER_NAME_LENGTH = 15;
  public static final int MAX_NUM_PLAYERS = 20;
  public static final String[] ALLOWED_ORIGINS = {
      "http://localhost:4200"
  };
}
