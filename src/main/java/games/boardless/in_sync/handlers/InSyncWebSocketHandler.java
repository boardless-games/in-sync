package games.boardless.in_sync.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import games.boardless.in_sync.services.InSyncService;

@Component
public class InSyncWebSocketHandler extends TextWebSocketHandler {
  private static final Logger logger = LoggerFactory.getLogger(InSyncWebSocketHandler.class);

  private final InSyncService inSyncService;

  @Autowired
  public InSyncWebSocketHandler(final InSyncService inSyncService) {
    this.inSyncService = inSyncService;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    this.inSyncService.connectionEstablished(session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

  }

  @Override
  protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {

  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    logger.error(String.format("Web socket transport error for session: %s.", session.getId()), exception);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }
}
