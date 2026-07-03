package games.boardless.in_sync.constants;

import java.util.stream.Stream;

public enum NoteDuration {
  EIGHTH(0.5f),
  QUARTER(1f),
  HALF(2f),
  WHOLE(4f);

  private final float beats;

  private NoteDuration(final float beats) {
    this.beats = beats;
  }

  public float getBeats() {
    return this.beats;
  }

  public NoteDuration fromDuration(final float beats) {
    return Stream.of(NoteDuration.values())
        .filter((final NoteDuration noteDuration) -> Float.compare(noteDuration.beats, beats) == 0)
        .findFirst()
        .orElseThrow();
  }
}
