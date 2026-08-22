package games.boardless.in_sync_server.constants;

import java.util.ArrayList;
import java.util.List;

import games.boardless.in_sync_server.models.Note;
import games.boardless.in_sync_server.models.SoundBoardButton;

public enum SongType {
  RANDOM(new ArrayList<SoundBoardButton>(), null);

  private final ArrayList<SoundBoardButton> buttons;
  private final ArrayList<Note> notes;

  private SongType(final ArrayList<SoundBoardButton> buttons, final ArrayList<Note> notes) {
    this.buttons = buttons;
    this.notes = notes;
  }

  public List<SoundBoardButton> getButtons() {
    return List.copyOf(this.buttons);
  }
}
