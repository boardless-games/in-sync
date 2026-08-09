package games.boardless.in_sync_server.constants;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NoteType {
  EIGHTH(0.5f),
  QUARTER(1f),
  HALF(2f),
  WHOLE(4f);

  private final float beats;

  private NoteType(final float beats) {
    this.beats = beats;
  }

  @JsonValue
  public float getBeats() {
    return this.beats;
  }

  public NoteType fromDuration(final float beats) {
    return Stream.of(NoteType.values())
        .filter((final NoteType noteDuration) -> Float.compare(noteDuration.beats, beats) == 0)
        .findFirst()
        .orElseThrow();
  }
}
