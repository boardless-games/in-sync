package games.boardless.in_sync.utils;

import org.springframework.web.socket.WebSocketSession;

public class ToString {
  public static String toString(final WebSocketSession session) {
    return String.format("WebSocketSession[id=%s, uri=%s]", session.getId(), session.getUri());
  }
}
