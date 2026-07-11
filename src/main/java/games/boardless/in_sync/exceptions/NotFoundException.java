package games.boardless.in_sync.exceptions;

public class NotFoundException extends Exception {
  public NotFoundException() {
    super();
  }

  public NotFoundException(final String message) {
    super(message);
  }

  public NotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public NotFoundException(final Throwable cause) {
    super(cause);
  }
}
