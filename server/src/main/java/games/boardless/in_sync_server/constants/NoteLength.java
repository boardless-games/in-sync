package games.boardless.in_sync_server.constants;

import java.util.stream.Stream;

public enum NoteLength {
  SIXTEENTH(0.25f),
  EIGHTH(0.5f),
  QUARTER(1f),
  DOTTED_QUARTER(1.5f),
  HALF(2f),
  DOTTED_HALF(3f),
  WHOLE(4f),
  DOTTED_WHOLE(6f);

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
