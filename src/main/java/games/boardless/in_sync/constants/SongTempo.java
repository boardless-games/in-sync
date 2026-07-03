package games.boardless.in_sync.constants;

import java.util.stream.Stream;

public enum SongTempo {
  LARGO(60),
  MODERATO(90),
  ALLEGRO(120),
  PRESTO(150);

  private final int tempo;

  private SongTempo(final int tempo) {
    this.tempo = tempo;
  }

  public int getTempo() {
    return this.tempo;
  }

  public SongTempo fromTempo(final int tempo) {
    return Stream.of(SongTempo.values())
        .filter((final SongTempo songTempo) -> songTempo.tempo == tempo)
        .findFirst()
        .orElseThrow();
  }
}
