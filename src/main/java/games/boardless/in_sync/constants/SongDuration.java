package games.boardless.in_sync.constants;

import java.util.stream.Stream;

public enum SongDuration {
  X_SHORT(5000),
  SHORT(10000),
  MEDIUM(15000),
  LONG(20000),
  X_LONG(25000);

  private final int duration;

  private SongDuration(final int duration) {
    this.duration = duration;
  }

  public int getDuration() {
    return this.duration;
  }

  public SongDuration fromDuration(final int duration) {
    return Stream.of(SongDuration.values())
        .filter((final SongDuration songDuration) -> songDuration.duration == duration)
        .findFirst()
        .orElseThrow();
  }
}
