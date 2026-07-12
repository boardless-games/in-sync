package games.boardless.in_sync.config;

import static games.boardless.in_sync.constants.Constants.ALLOWED_ORIGINS;

import games.boardless.in_sync.handlers.InSyncWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  private final InSyncWebSocketHandler handler;

  @Autowired
  public WebSocketConfig(final InSyncWebSocketHandler handler) {
    this.handler = handler;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(this.handler, "/in-sync").setAllowedOrigins(ALLOWED_ORIGINS);
  }
}
