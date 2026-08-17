package games.boardless.in_sync_server.dtos;

import games.boardless.in_sync_server.constants.SongDuration;
import games.boardless.in_sync_server.constants.SongTempo;
import games.boardless.in_sync_server.constants.SongType;

public record SongSettingsDto(
    SongType songType, SongTempo songTempo, SongDuration songDuration, boolean metronome, boolean randomPlayerOrder) {}
