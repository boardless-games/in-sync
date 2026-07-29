package games.boardless.in_sync_server.handlers;

import games.boardless.in_sync_server.services.InSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class InSyncWebSocketHandler extends TextWebSocketHandler {
  private static final Logger logger = LoggerFactory.getLogger(InSyncWebSocketHandler.class);

  public static final String getWebSocketSessionString(final WebSocketSession session) {
    return String.format("WebSocketSession[id=%s, uri=%s]", session.getId(), session.getUri());
  }

  private final InSyncService inSyncService;

  @Autowired
  public InSyncWebSocketHandler(final InSyncService inSyncService) {
    this.inSyncService = inSyncService;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    this.inSyncService.connect(session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    logger.info("{} sent {}.", getWebSocketSessionString(session), message.getPayload());
  }

  @Override
  protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
    this.inSyncService.handlePongMessage(session);
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    logger.error(
        "Web socket transport error on {}.", getWebSocketSessionString(session), exception);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
      throws Exception {
    this.inSyncService.disconnect(session);
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }
}
