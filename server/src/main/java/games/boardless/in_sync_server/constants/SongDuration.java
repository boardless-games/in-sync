package games.boardless.in_sync_server.constants;

import java.util.stream.Stream;

public enum SongDuration {
  X_SHORT(10_000),
  SHORT(15_000),
  MEDIUM(20_000),
  LONG(25_000),
  X_LONG(30_000);

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
