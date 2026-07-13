package games.boardless.in_sync_server.dtos;

import games.boardless.in_sync_server.constants.ScheduleType;

public record ScheduleDto(ScheduleType type, long schedule) {}
