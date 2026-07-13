package games.boardless.in_sync_server.constants;

import java.util.stream.Stream;

public enum NoteFrequency {
  C1(32.2032f),
  C2(65.40639f),
  C3(130.8128f),
  C4(261.6256f),
  C5(523.2511f),
  C6(1046.502f),
  C7(2093.005f),
  C8(4186.009f);

  private float frequency;

  private NoteFrequency(final float frequency) {
    this.frequency = frequency;
  }

  public float getFrequency() {
    return this.frequency;
  }

  public NoteFrequency fromFrequency(final float frequency) {
    return Stream.of(NoteFrequency.values())
        .filter(
            (final NoteFrequency noteFrequency) ->
                Float.compare(noteFrequency.frequency, frequency) == 0)
        .findFirst()
        .orElseThrow();
  }
}
