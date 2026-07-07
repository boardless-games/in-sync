package games.boardless.in_sync.models;

public class PerformanceNote {
  private final boolean status;
  private final long offset;

  public PerformanceNote(final long target, final long actual, final long allowedMargin) {
    this.offset = actual - target;
    this.status = Math.abs(this.offset) <= allowedMargin;
  }

  public boolean getStatus() {
    return this.status;
  }

  public long getOffset() {
    return this.offset;
  }
}
