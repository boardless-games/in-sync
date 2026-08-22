package games.boardless.in_sync_server.constants;

import java.util.stream.Stream;

public enum NoteLength {
  EIGHTH(0.5f),
  QUARTER(1f),
  HALF(2f),
  WHOLE(4f);

  private final float beats;

  private NoteLength(final float beats) {
    this.beats = beats;
  }

  public float getBeats() {
    return this.beats;
  }

  public NoteLength fromDuration(final float beats) {
    return Stream.of(NoteLength.values())
        .filter((final NoteLength noteDuration) -> Float.compare(noteDuration.beats, beats) == 0)
        .findFirst()
        .orElseThrow();
  }
}
