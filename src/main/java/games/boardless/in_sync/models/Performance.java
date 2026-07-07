package games.boardless.in_sync.models;

import java.util.ArrayList;

public class Performance {
  private final String playerName;
  private final boolean status;
  private final ArrayList<PerformanceNote> notes = new ArrayList<>();

  public Performance(final String playerName, final Song song, final long[] performance) {
    this.playerName = playerName;
    // final long[] targets = song.getNotes().stream().filter(note ->
    // note.getPlayerAssignment().equals(playerName)).map(note -> note.get)
  }
}
