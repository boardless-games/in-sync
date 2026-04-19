package games.boardless.in_sync.utils;

import java.util.Optional;
import java.util.regex.Pattern;

public class InputValidation {
  private static int MIN_NAME_LENGTH = 3;
  private static int MAX_NAME_LENGTH = 15;

  /**
   * Returns an Optional with a String explanation if the provided name
   * is invalid. If valid, the Optional will be empty.
   * 
   */
  public static Optional<String> validateName(final String name) {
    if (name == null) {
      return Optional.of("Name must be defined.");
    } else if (name.isBlank()) {
      return Optional.of("Name cannot be empty.");
    } else if (name.length() != name.trim().length()) {
      return Optional.of("Name cannot contain extra whitespace.");
    } else if (name.length() < MIN_NAME_LENGTH) {
      return Optional.of(String.format("Name must be at least %d characters long.", MIN_NAME_LENGTH));
    } else if (name.length() > MAX_NAME_LENGTH) {
      return Optional.of(String.format("Name cannot be longer than %d characters.", MAX_NAME_LENGTH));
    } else if (!Pattern.matches("^[a-zA-Z0-9]+$", name)) {
      return Optional.of("Name can only contain letters and numbers.");
    }
    return Optional.empty();
  }
}
