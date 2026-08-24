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
      null,
      null),
    HEART_AND_SOUL(
      "Heart and Soul",
      new SoundBoardButton[][] {

      },
      SongTempo.LARGO,
      new Note[][] {
        {
          // 1
          new Note(NoteSound.PIANO, NoteFrequency.C5, NoteLength.QUARTER, null, 0),
          new Note(NoteSound.PIANO, NoteFrequency.C5, NoteLength.QUARTER, null, 1000),
          new Note(NoteSound.PIANO, NoteFrequency.C5, NoteLength.HALF, null, 2000),
          // 2
          new Note(NoteSound.PIANO, NoteFrequency.C5, NoteLength.EIGHTH, null, 4500),
          new Note(NoteSound.PIANO, NoteFrequency.B4, NoteLength.EIGHTH, null, 5000),
          new Note(NoteSound.PIANO, NoteFrequency.A4, NoteLength.EIGHTH, null, 5500),
          new Note(NoteSound.PIANO, NoteFrequency.B4, NoteLength.EIGHTH, null, 6000),
          new Note(NoteSound.PIANO, NoteFrequency.C5, NoteLength.EIGHTH, null, 6500),
          new Note(NoteSound.PIANO, NoteFrequency.D5, NoteLength.QUARTER, null, 7000),
          // 3
          new Note(NoteSound.PIANO, NoteFrequency.E5, NoteLength.QUARTER, null, 8000),
          new Note(NoteSound.PIANO, NoteFrequency.E5, NoteLength.QUARTER, null, 9000),
          new Note(NoteSound.PIANO, NoteFrequency.E5, NoteLength.HALF, null, 10_000),
          // 4
        }
      }
    );

  private final String title;
  private final SoundBoardButton[][] buttons;
  private final SongTempo baseTempo;
  private final Note[][] notes;

  private Song(final String title, final SoundBoardButton[][] buttons, final SongTempo baseTempo, final Note[][] notes) {
    this.title = title;
    this.buttons = buttons;
    this.baseTempo = baseTempo;
    this.notes = notes;
  }

  public String getTitle() {
    return this.title;
  }

  public SoundBoardButton[][] getButtons() {
    return this.buttons;
  }

  public SongTempo getBaseTempo() {
    return this.baseTempo;
  }

  public Note[][] getNotes() {
    return this.notes;
  }

  public Song fromTitle(final String title) {
    return Stream.of(Song.values())
        .filter((final Song song) -> song.title.equals(title))
        .findFirst()
        .orElseThrow();
  }
}
