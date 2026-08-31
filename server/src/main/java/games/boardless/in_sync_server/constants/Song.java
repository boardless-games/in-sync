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
  HEART_AND_SOUL_BASIC(
      "Heart and Soul (Basic)",
      new SoundBoardButton[][] {

      },
      SongTempo.LARGO,
      new Note[][][] {
          {
              {
                  new Note(NoteSound.PIANO, NoteFrequency.C5, NoteLength.QUARTER, null, 0),
                  new Note(NoteSound.PIANO, NoteFrequency.C5, NoteLength.QUARTER, null, 1000),
                  new Note(NoteSound.PIANO, NoteFrequency.C5, NoteLength.HALF, null, 2000),
              },
              {
                  new Note(NoteSound.PIANO, NoteFrequency.C5, NoteLength.EIGHTH, null, 500),
                  new Note(NoteSound.PIANO, NoteFrequency.B4, NoteLength.EIGHTH, null, 1000),
                  new Note(NoteSound.PIANO, NoteFrequency.A4, NoteLength.EIGHTH, null, 1500),
                  new Note(NoteSound.PIANO, NoteFrequency.B4, NoteLength.EIGHTH, null, 2000),
                  new Note(NoteSound.PIANO, NoteFrequency.C5, NoteLength.EIGHTH, null, 2500),
                  new Note(NoteSound.PIANO, NoteFrequency.D5, NoteLength.QUARTER, null, 3000),
              },
              {
                  new Note(NoteSound.PIANO, NoteFrequency.E5, NoteLength.QUARTER, null, 0),
                  new Note(NoteSound.PIANO, NoteFrequency.E5, NoteLength.QUARTER, null, 1000),
                  new Note(NoteSound.PIANO, NoteFrequency.E5, NoteLength.HALF, null, 2000),
              },
              {
                  new Note(NoteSound.PIANO, NoteFrequency.E5, NoteLength.EIGHTH, null, 500),
                  new Note(NoteSound.PIANO, NoteFrequency.D5, NoteLength.EIGHTH, null, 1000),
                  new Note(NoteSound.PIANO, NoteFrequency.C5, NoteLength.EIGHTH, null, 1500),
                  new Note(NoteSound.PIANO, NoteFrequency.D5, NoteLength.EIGHTH, null, 2000),
                  new Note(NoteSound.PIANO, NoteFrequency.E5, NoteLength.EIGHTH, null, 2500),
                  new Note(NoteSound.PIANO, NoteFrequency.F5, NoteLength.QUARTER, null, 3000),
              },
              {
                new Note(NoteSound.PIANO, NoteFrequency.G5, NoteLength.HALF, null, 0),
                new Note(NoteSound.PIANO, NoteFrequency.C4, NoteLength.HALF, null, 2000),
              },
              {
                new Note(NoteSound.PIANO, NoteFrequency.A5, NoteLength.EIGHTH, null, 500),
                new Note(NoteSound.PIANO, NoteFrequency.G5, NoteLength.EIGHTH, null, 1000),
                new Note(NoteSound.PIANO, NoteFrequency.F5, NoteLength.EIGHTH, null, 1500),
                new Note(NoteSound.PIANO, NoteFrequency.E5, NoteLength.QUARTER, null, 2000),
                new Note(NoteSound.PIANO, NoteFrequency.D5, NoteLength.QUARTER, null, 3000),
              },
              {
                new Note(NoteSound.PIANO, NoteFrequency.C5, NoteLength.QUARTER, null, 0),
                new Note(NoteSound.PIANO, NoteFrequency.B4, NoteLength.EIGHTH, null, 1500),
                new Note(NoteSound.PIANO, NoteFrequency.A4, NoteLength.QUARTER, null, 2000),
                new Note(NoteSound.PIANO, NoteFrequency.G4, NoteLength.EIGHTH, null, 3500),
              },
              {
                new Note(NoteSound.PIANO, NoteFrequency.F4, NoteLength.QUARTER, null, 0),
                new Note(NoteSound.PIANO, NoteFrequency.G4, NoteLength.EIGHTH, null, 1500),
                new Note(NoteSound.PIANO, NoteFrequency.A4, NoteLength.QUARTER, null, 2000),
                new Note(NoteSound.PIANO, NoteFrequency.B4, NoteLength.QUARTER, null, 3000),
              },
          },
          {
            {
              new Note(NoteSound.PIANO, NoteFrequency.C4, NoteLength.HALF, null, 0),
              new Note(NoteSound.PIANO, NoteFrequency.A3, NoteLength.HALF, null, 2000),
            },
            {
              new Note(NoteSound.PIANO, NoteFrequency.F3, NoteLength.HALF, null, 0),
              new Note(NoteSound.PIANO, NoteFrequency.G3, NoteLength.HALF, null, 2000),
            },
            {
              new Note(NoteSound.PIANO, NoteFrequency.C4, NoteLength.HALF, null, 0),
              new Note(NoteSound.PIANO, NoteFrequency.A3, NoteLength.HALF, null, 2000),
            },
            {
              new Note(NoteSound.PIANO, NoteFrequency.F3, NoteLength.HALF, null, 0),
              new Note(NoteSound.PIANO, NoteFrequency.G3, NoteLength.HALF, null, 2000),
            },
            {
              new Note(NoteSound.PIANO, NoteFrequency.C4, NoteLength.HALF, null, 0),
              new Note(NoteSound.PIANO, NoteFrequency.A3, NoteLength.HALF, null, 2000),
            },
            {
              new Note(NoteSound.PIANO, NoteFrequency.F3, NoteLength.HALF, null, 0),
              new Note(NoteSound.PIANO, NoteFrequency.G3, NoteLength.HALF, null, 2000),
            },
            {
              new Note(NoteSound.PIANO, NoteFrequency.C4, NoteLength.HALF, null, 0),
              new Note(NoteSound.PIANO, NoteFrequency.A3, NoteLength.HALF, null, 2000),
            },
            {
              new Note(NoteSound.PIANO, NoteFrequency.F3, NoteLength.HALF, null, 0),
              new Note(NoteSound.PIANO, NoteFrequency.G3, NoteLength.HALF, null, 2000),
            },
          }
      });

  private final String title;
  private final SoundBoardButton[][] buttons;
  private final SongTempo baseTempo;
  private final Note[][][] notes; // Parts -> Measures -> Notes

  private Song(final String title, final SoundBoardButton[][] buttons, final SongTempo baseTempo,
      final Note[][][] notes) {
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

  public Note[][][] getNotes() {
    return this.notes;
  }

  public Song fromTitle(final String title) {
    return Stream.of(Song.values())
        .filter((final Song song) -> song.title.equals(title))
        .findFirst()
        .orElseThrow();
  }
}
