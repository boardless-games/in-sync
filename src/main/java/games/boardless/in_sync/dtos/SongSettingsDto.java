package games.boardless.in_sync.dtos;

import games.boardless.in_sync.constants.SongDuration;
import games.boardless.in_sync.constants.SongTempo;
import games.boardless.in_sync.constants.SongType;

public record SongSettingsDto(
    SongType songType, SongTempo songTempo, SongDuration songDuration, boolean randomPlayerOrder) {}
