package games.boardless.in_sync_server.dtos;

import games.boardless.in_sync_server.constants.MessageTopic;

public record MessageDto(MessageTopic topic, Object data) {}
