package games.boardless.in_sync_server.dtos;

import games.boardless.in_sync_server.constants.Song;
import games.boardless.in_sync_server.constants.SongTempo;

public record SongSettingsDto(Song song, SongTempo songTempo) {}
