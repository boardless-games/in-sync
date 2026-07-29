package games.boardless.in_sync_server.config;

import games.boardless.in_sync_server.handlers.InSyncWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  private final InSyncWebSocketHandler handler;

  @Value("${client.urls}")
  private String clientUrls;

  @Autowired
  public WebSocketConfig(final InSyncWebSocketHandler handler) {
    this.handler = handler;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(this.handler, "/in-sync-ws").setAllowedOrigins(clientUrls.split(","));
  }
}
