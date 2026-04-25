package games.boardless.in_sync.models;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class Player {
  private static final Logger logger = LoggerFactory.getLogger(Player.class);
  private final String name;
  private WebSocketSession session;
  private boolean ready;

  public Player(final String name) {
    this.name = name;
    this.session = null;
    this.ready = false;
  }

  public String getName() {
    return this.name;
  }

  public void setSession(final WebSocketSession session) {
    this.session = session;
  }

  public boolean isConnected() {
    return this.session != null;
  }

  public boolean isReady() {
    return this.isConnected() && this.ready;
  }

  @Override
  public String toString() {
    return "Player [name=" + name + ", sessionId=" + this.session != null ? this.session.getId() : null + ", ready=" + this.ready + "]";
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
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Player other = (Player) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  public void sendMessage(final TextMessage message) {
    if (this.session == null)
      return;

    try {
      this.session.sendMessage(message);
    } catch (IOException e) {
      logger.error(
          String.format("Failed to send message (%s) to %s.", message.getPayload(), this.toString()), e);
    }
  }

  public void ping() {
    this.ready = false;
    if (this.session == null)
      return;
    try {
      this.session.sendMessage(new PingMessage());
    } catch (IOException e) {
      logger.error(
          String.format("Failed to ping %s.", this.toString()), e);
    }
  }

  public void pong() {
    if (this.session == null)
      return;
    this.ready = true;
  }
}
