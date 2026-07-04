package games.boardless.in_sync.utils;

import games.boardless.in_sync.models.Message;
import org.springframework.web.socket.TextMessage;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public final class ToTextMessage {
  private ToTextMessage() {}

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static TextMessage toTextMessage(final Message message) throws JacksonException {
    return new TextMessage(ToTextMessage.objectMapper.writeValueAsString(message));
  }
}
