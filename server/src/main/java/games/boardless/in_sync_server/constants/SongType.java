package games.boardless.in_sync_server.constants;

public enum SongType {
  RANDOM(new NoteSound[]{NoteSound.HIHAT_CLOSED, NoteSound.CLAP, NoteSound.KICK, NoteSound.SNARE_ELECTRIC});

  private final NoteSound[] availableSounds;

  private SongType(final NoteSound[] availableSounds) {
    this.availableSounds = availableSounds;
  }

  public NoteSound[] getAvailableSounds() {
    return this.availableSounds;
  }
}
