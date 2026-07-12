package games.boardless.in_sync.dtos;

import games.boardless.in_sync.constants.ScheduleType;

public record ScheduleDto(ScheduleType type, long schedule) {}
