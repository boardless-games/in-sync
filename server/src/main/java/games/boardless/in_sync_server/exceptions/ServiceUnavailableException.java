package games.boardless.in_sync_server.exceptions;

public class ServiceUnavailableException extends Exception {
  public ServiceUnavailableException() {
    super();
  }

  public ServiceUnavailableException(final String message) {
    super(message);
  }

  public ServiceUnavailableException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ServiceUnavailableException(final Throwable cause) {
    super(cause);
  }
}
