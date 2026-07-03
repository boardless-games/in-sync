package games.boardless.in_sync.models;

import static games.boardless.in_sync.constants.Constants.MILLIS_PER_MINUTE;

import games.boardless.in_sync.constants.GameDifficulty;
import games.boardless.in_sync.constants.GameType;
import games.boardless.in_sync.constants.NoteDuration;
import games.boardless.in_sync.constants.NoteFrequency;
import games.boardless.in_sync.constants.NoteSound;
import games.boardless.in_sync.constants.SongTempo;
import java.util.ArrayList;
import java.util.Random;

public class Song {
  private final SongTempo tempo;
  private final ArrayList<Note> notes = new ArrayList<>();

  public Song(final GameType type, final GameDifficulty difficulty) {
    int length; // Milliseconds
    if (difficulty == GameDifficulty.EASY) {
      this.tempo = SongTempo.LARGO;
      length = 10000;
    } else if (difficulty == GameDifficulty.MEDIUM) {
      this.tempo = SongTempo.MODERATO;
      length = 8000;
    } else if (difficulty == GameDifficulty.HARD) {
      this.tempo = SongTempo.ALLEGRO;
      length = 7000;
    } else if (difficulty == GameDifficulty.EXPERT) {
      this.tempo = SongTempo.PRESTO;
      length = 6000;
    } else {
      throw new IllegalArgumentException("Invalid game difficulty.");
    }

    // Create notes
    final Random rand = new Random();
    for (int currentLength = 0; currentLength < length; ) {
      final NoteSound noteSound = NoteSound.MAIN;
      final NoteDuration noteDuration =
          NoteDuration.values()[rand.nextInt(NoteDuration.values().length)];
      final NoteFrequency noteFrequency = NoteFrequency.C4;
      this.notes.add(new Note(noteSound, noteDuration, noteFrequency));

      currentLength += (1f / this.tempo.getTempo()) * MILLIS_PER_MINUTE;
    }
  }
}
