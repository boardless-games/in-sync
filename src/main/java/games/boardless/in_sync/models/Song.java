package games.boardless.in_sync.models;

import games.boardless.in_sync.constants.GameDifficulty;
import games.boardless.in_sync.constants.NoteDuration;
import games.boardless.in_sync.constants.NoteFrequency;
import games.boardless.in_sync.constants.NoteSound;
import games.boardless.in_sync.constants.SongTempo;
import java.util.ArrayList;
import java.util.Random;

public class Song {
  private final SongTempo tempo;
  private final int length; // Milliseconds
  private final ArrayList<Note> notes = new ArrayList<>();

  public Song(final GameDifficulty difficulty) {
    if (difficulty == GameDifficulty.EASY) {
      this.tempo = SongTempo.LARGO;
      this.length = 10000;
    } else if (difficulty == GameDifficulty.MEDIUM) {
      this.tempo = SongTempo.MODERATO;
      this.length = 8000;
    } else if (difficulty == GameDifficulty.HARD) {
      this.tempo = SongTempo.ALLEGRO;
      this.length = 7000;
    } else if (difficulty == GameDifficulty.EXPERT) {
      this.tempo = SongTempo.PRESTO;
      this.length = 6000;
    } else {
      throw new IllegalArgumentException("Invalid game difficulty.");
    }

    this.createSong();
  }

  private void createSong() {
    final Random rand = new Random();
    for (int songLength = 0; songLength < this.length; ) {
      final NoteSound noteSound = NoteSound.MAIN;
      final NoteDuration noteDuration =
          NoteDuration.values()[rand.nextInt(NoteDuration.values().length)];
      final NoteFrequency noteFrequency = NoteFrequency.C4;
      this.notes.add(new Note(noteSound, noteDuration, noteFrequency));
    }
  }
}
