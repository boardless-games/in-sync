package games.boardless.in_sync.utils;

import static games.boardless.in_sync.constants.Constants.MAX_PLAYER_NAME_LENGTH;
import static games.boardless.in_sync.constants.Constants.MIN_PLAYER_NAME_LENGTH;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InputValidationTests {
  @Test
  void validateName_withNullName_shouldBePresent() {
    assertTrue(InputValidation.validatePlayerName(null).isPresent());
  }

  @Test
  void validateName_withBlankName_shouldBePresent() {
    assertTrue(InputValidation.validatePlayerName("").isPresent());
    assertTrue(InputValidation.validatePlayerName("\t").isPresent());
  }

  @Test
  void validateName_withTrailingWhiteSpaceInName_shouldBePresent() {
    assertTrue(InputValidation.validatePlayerName("  test").isPresent());
    assertTrue(InputValidation.validatePlayerName("test ").isPresent());
    assertTrue(InputValidation.validatePlayerName("test\t").isPresent());
  }

  @Test
  void validateName_withShortName_shouldBePresent() {
    final String name = "a".repeat(Math.max(0, MIN_PLAYER_NAME_LENGTH - 1));
    assertTrue(InputValidation.validatePlayerName(name).isPresent());
  }

  @Test
  void validateName_withLongName_shouldBePresent() {
    final String name = "a".repeat(MAX_PLAYER_NAME_LENGTH + 1);
    assertTrue(InputValidation.validatePlayerName(name).isPresent());
  }

  @Test
  void validateName_withSpecialChars_shouldBePresent() {
    final String name = "a".repeat(MIN_PLAYER_NAME_LENGTH);
    assertTrue(InputValidation.validatePlayerName(name + "!").isPresent());
    assertTrue(InputValidation.validatePlayerName(name + "@").isPresent());
    assertTrue(InputValidation.validatePlayerName(name + "-").isPresent());
    assertTrue(InputValidation.validatePlayerName(name + "_").isPresent());
  }

  @Test
  void validateName_shouldBeEmpty() {
    assertTrue(InputValidation.validatePlayerName("a".repeat(MIN_PLAYER_NAME_LENGTH)).isEmpty());
    assertTrue(InputValidation.validatePlayerName("a".repeat(MAX_PLAYER_NAME_LENGTH)).isEmpty());
    assertTrue(InputValidation.validatePlayerName("1".repeat(MIN_PLAYER_NAME_LENGTH)).isEmpty());
    assertTrue(InputValidation.validatePlayerName("1".repeat(MAX_PLAYER_NAME_LENGTH)).isEmpty());
  }
}
