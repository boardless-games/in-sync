package games.boardless.in_sync.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import games.boardless.in_sync.services.InSyncService;

@ExtendWith(MockitoExtension.class)
class InSyncWebSocketHandlerTests {
  @Mock
  private InSyncService inSyncService;

  @InjectMocks
  private InSyncWebSocketHandler inSyncWebSocketHandler;

  @Test
  void afterConnectionEstablished_shouldConnect() throws Exception {
    final WebSocketSession session = mock();
    this.inSyncWebSocketHandler.afterConnectionEstablished(session);
    verify(this.inSyncService).connect(any(WebSocketSession.class));
  }

  @Test
  @Disabled
  void handleTextMessage_shouldHandle() throws Exception {
    final WebSocketSession session = mock();
    this.inSyncWebSocketHandler.handleTextMessage(session, new TextMessage("Test"));
  }

  @Test
  void handlePongMessage_shouldHandlePongMessage() throws Exception {
    final WebSocketSession session = mock();
    final PongMessage pongMessage = mock();
    this.inSyncWebSocketHandler.handlePongMessage(session, pongMessage);
    verify(this.inSyncService).handlePongMessage(any(WebSocketSession.class));
  }

  @Test
  void handleTransportError_shouldLogError() throws Exception {
    final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();

    final Logger logger = (Logger) LoggerFactory.getLogger(InSyncWebSocketHandler.class);
    logger.addAppender(listAppender);

    try {
      final WebSocketSession session = mock();
      final Throwable exception = mock();
      this.inSyncWebSocketHandler.handleTransportError(session, exception);

      final ILoggingEvent log = listAppender.list.getFirst();
      assertTrue(log.getFormattedMessage().contains("Web socket transport error"));
    } finally {
      logger.detachAppender(listAppender);
      listAppender.stop();
    }
  }

  @Test
  void afterConnectionClosed_shouldDisconnect() throws Exception {
    final WebSocketSession session = mock();
    final CloseStatus closeStatus = mock();
    this.inSyncWebSocketHandler.afterConnectionClosed(session, closeStatus);
    verify(this.inSyncService).disconnect(any(WebSocketSession.class));
  }

  @Test
  void supportsPartialMessages_shouldReturnFalse() throws Exception {
    assertFalse(this.inSyncWebSocketHandler.supportsPartialMessages());
  }
}
