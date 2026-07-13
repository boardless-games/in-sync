package games.boardless.in_sync_server.models;

import games.boardless.in_sync_server.constants.MessageTopic;

public class Message {
  private final MessageTopic topic;
  private final Object data;

  public Message(final MessageTopic topic, final Object data) {
    this.topic = topic;
    this.data = data;
  }

  public MessageTopic getTopic() {
    return this.topic;
  }

  public Object getData() {
    return this.data;
  }
}
