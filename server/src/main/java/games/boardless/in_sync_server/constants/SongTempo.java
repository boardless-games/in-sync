package games.boardless.in_sync_server.constants;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SongTempo {
  // All values should be divisible by 2 and 4
  LARGO(1000), // 60 bpm
  MODERATO(600), // 100 bpm
  ALLEGRO(500), // 120 bpm
  PRESTO(400); // 150 bpm

  private final int millisPerBeat;

  private SongTempo(final int millisPerBeat) {
    this.millisPerBeat = millisPerBeat;
  }

  @JsonValue
  public int getMillisPerBeat() {
    return this.millisPerBeat;
  }

  public SongTempo fromTempo(final int tempo) {
    return Stream.of(SongTempo.values())
        .filter((final SongTempo songTempo) -> songTempo.millisPerBeat == tempo)
        .findFirst()
        .orElseThrow();
  }
}
