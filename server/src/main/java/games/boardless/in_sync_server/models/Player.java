package games.boardless.in_sync_server.models;

import games.boardless.in_sync_server.handlers.InSyncWebSocketHandler;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class Player implements Comparable<Player> {
  private static final Logger logger = LoggerFactory.getLogger(Player.class);
  private final String name;
  private final int position;
  private WebSocketSession session;
  private boolean ready;

  public Player(final String name, final int position) {
    this.name = name;
    this.position = position;
    this.session = null;
    this.ready = false;
  }

  public String getName() {
    return this.name;
  }

  public int getPosition() {
    return this.position;
  }

  public void connect(final WebSocketSession session) {
    this.session = session;
  }

  public void disconnect() {
    if (this.isConnected()) {
      try {
        this.session.close();
      } catch (IOException e) {
        logger.error("Failed to disconnect {}.", this.name);
      }
    }
    this.session = null;
  }

  public boolean isConnected() {
    return this.session != null && this.session.isOpen();
  }

  public boolean isReady() {
    return this.isConnected() && this.ready;
  }

  public void setReady(final boolean ready) {
    this.ready = ready;
  }

  @Override
  public String toString() {
    return "Player [name="
        + name
        + ", position="
        + position
        + ", session="
        + InSyncWebSocketHandler.getWebSocketSessionString(session)
        + ", ready="
        + ready
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Player other = (Player) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    return true;
  }

  public void message(final TextMessage message) {
    if (!this.isConnected()) {
      return;
    }

    try {
      this.session.sendMessage(message);
    } catch (IOException e) {
      logger.error("Failed to send a message({}) to {}.", message.getPayload(), this.toString(), e);
    }
  }

  public void ping() {
    this.ready = false;
    if (!this.isConnected()) {
      return;
    }

    try {
      this.session.sendMessage(new PingMessage());
    } catch (IOException e) {
      logger.error("Failed to ping {}.", this.toString(), e);
    }
  }

  @Override
  public int compareTo(Player other) {
    return this.position - other.getPosition();
  }
}
