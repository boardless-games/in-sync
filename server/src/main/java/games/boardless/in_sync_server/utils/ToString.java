package games.boardless.in_sync_server.utils;

import org.springframework.web.socket.WebSocketSession;

public final class ToString {
  private ToString() {}

  public static String toString(final WebSocketSession session) {
    return String.format("WebSocketSession[id=%s, uri=%s]", session.getId(), session.getUri());
  }

  public static String toString(final Exception exception, final int stackTraceLimit) {
    final StringBuilder sb = new StringBuilder();
    sb.append(exception);
    final StackTraceElement[] stackTraceElements = exception.getStackTrace();
    final int limit = Math.min(stackTraceElements.length, stackTraceLimit);
    for (int i = 0; i < limit; ++i) {
      sb.append("\n\tat ").append(stackTraceElements[i]);
    }
    return sb.toString();
  }
}
