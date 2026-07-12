package games.boardless.in_sync.models;

import games.boardless.in_sync.dtos.PerformanceDto;
import java.util.ArrayList;
import java.util.List;

public class Performance {
  private final String playerName;
  private final boolean status;
  private final ArrayList<PerformanceNote> notes = new ArrayList<>();

  public Performance(final String playerName, final Song song, final PerformanceDto performance) {
    this.playerName = playerName;

    final long performanceSchedule = performance.schedule();
    final long[] targets =
        song.getNotes().stream()
            .filter(note -> note.getPlayerAssignment().equals(playerName))
            .mapToLong(note -> note.getSchedule())
            .toArray();
    final long[] actuals = performance.performance();

    boolean performanceStatus = true;
    for (int i = 0; i < targets.length; ++i) {
      PerformanceNote performanceNote;
      if (i >= actuals.length) {
        performanceNote = new PerformanceNote(targets[i], 0l, 0l);
      } else {
        performanceNote = new PerformanceNote(targets[i], actuals[i] - performanceSchedule, 50l);
      }

      if (!performanceNote.getStatus()) {
        performanceStatus = false;
      }

      this.notes.add(performanceNote);
    }
    this.status = performanceStatus;
  }

  public String getPlayerName() {
    return this.playerName;
  }

  public boolean getStatus() {
    return this.status;
  }

  public List<PerformanceNote> getNotes() {
    return List.copyOf(this.notes);
  }
}
