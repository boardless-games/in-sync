package games.boardless.in_sync.utils;

import static games.boardless.in_sync.constants.Constants.GAME_CODE_LENGTH;
import static games.boardless.in_sync.constants.Constants.MAX_PLAYER_NAME_LENGTH;
import static games.boardless.in_sync.constants.Constants.MIN_PLAYER_NAME_LENGTH;

import java.util.Optional;
import java.util.regex.Pattern;

public final class InputValidation {
  private InputValidation() {}

  /**
   * Returns an Optional with a String explanation if the provided name is invalid. If valid, the
   * Optional will be empty.
   */
  public static Optional<String> validateName(final String name) {
    if (name == null) {
      return Optional.of("Name must be defined.");
    } else if (name.isBlank()) {
      return Optional.of("Name cannot be empty.");
    } else if (name.length() != name.trim().length()) {
      return Optional.of("Name cannot contain extra whitespace.");
    } else if (name.length() < MIN_PLAYER_NAME_LENGTH) {
      return Optional.of(
          String.format("Name must be at least %d characters long.", MIN_PLAYER_NAME_LENGTH));
    } else if (name.length() > MAX_PLAYER_NAME_LENGTH) {
      return Optional.of(
          String.format("Name cannot be longer than %d characters.", MAX_PLAYER_NAME_LENGTH));
    } else if (!Pattern.matches("^[a-zA-Z0-9]+$", name)) {
      return Optional.of("Name can only contain letters and numbers.");
    }
    return Optional.empty();
  }

  /**
   * Returns an Optional with a String explanation if the provided game code is invalid. If valid,
   * the Optional will be empty.
   */
  public static Optional<String> validateGameCode(final String gameCode) {
    if (gameCode == null) {
      return Optional.of("Game code must be defined.");
    } else if (gameCode.isBlank()) {
      return Optional.of("Game code cannot be empty.");
    } else if (gameCode.length() != GAME_CODE_LENGTH || !Pattern.matches("^[0-9]+$", gameCode)) {
      return Optional.of(String.format("Game code must be %d digits.", GAME_CODE_LENGTH));
    }
    return Optional.empty();
  }
}
