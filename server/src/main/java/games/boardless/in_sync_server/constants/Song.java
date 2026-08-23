package games.boardless.in_sync_server.constants;

import games.boardless.in_sync_server.models.Note;
import games.boardless.in_sync_server.models.SoundBoardButton;
import java.util.stream.Stream;

public enum Song {
  RANDOM(
      "A Random Song",
      new SoundBoardButton[][] {
        {
          new SoundBoardButton(NoteSound.HIHAT_CLOSED, Color.PINK, "hihat"),
          new SoundBoardButton(NoteSound.CLAP, Color.RED, "clap")
        },
        {
          new SoundBoardButton(NoteSound.KICK, Color.YELLOW, "kick"),
          new SoundBoardButton(NoteSound.SNARE_ELECTRIC, Color.GREEN, "snare")
        },
      },
      null);

  private final String title;
  private final SoundBoardButton[][] buttons;
  private final Note[] notes;

  private Song(final String title, final SoundBoardButton[][] buttons, final Note[] notes) {
    this.title = title;
    this.buttons = buttons;
    this.notes = notes;
  }

  public String getTitle() {
    return this.title;
  }

  public SoundBoardButton[][] getButtons() {
    return this.buttons;
  }

  public Note[] getNotes() {
    return this.notes;
  }

  public Song fromTitle(final String title) {
    return Stream.of(Song.values())
        .filter((final Song song) -> song.title.equals(title))
        .findFirst()
        .orElseThrow();
  }
}
