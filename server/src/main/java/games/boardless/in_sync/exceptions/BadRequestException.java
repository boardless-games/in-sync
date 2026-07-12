package games.boardless.in_sync.exceptions;

public class BadRequestException extends Exception {
  public BadRequestException() {
    super();
  }

  public BadRequestException(final String message) {
    super(message);
  }

  public BadRequestException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public BadRequestException(final Throwable cause) {
    super(cause);
  }
}
