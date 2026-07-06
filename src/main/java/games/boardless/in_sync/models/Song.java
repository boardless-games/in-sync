package games.boardless.in_sync.models;

import static games.boardless.in_sync.constants.Constants.MILLIS_PER_MINUTE;

import games.boardless.in_sync.constants.GameDifficulty;
import games.boardless.in_sync.constants.GameType;
import games.boardless.in_sync.constants.NoteFrequency;
import games.boardless.in_sync.constants.NoteSound;
import games.boardless.in_sync.constants.NoteType;
import games.boardless.in_sync.constants.SongTempo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Song {
  private final SongTempo tempo;
  private final ArrayList<Note> notes = new ArrayList<>();

  public Song(
      final GameType type, final GameDifficulty difficulty, final ArrayList<String> playerNames) {
    float duration; // Milliseconds
    boolean randomPlayerOrder;
    if (difficulty == GameDifficulty.EASY) {
      this.tempo = SongTempo.LARGO;
      duration = 10000f;
      randomPlayerOrder = false;
    } else if (difficulty == GameDifficulty.MEDIUM) {
      this.tempo = SongTempo.MODERATO;
      duration = 8000f;
      randomPlayerOrder = false;
    } else if (difficulty == GameDifficulty.HARD) {
      this.tempo = SongTempo.ALLEGRO;
      duration = 7000f;
      randomPlayerOrder = true;
    } else if (difficulty == GameDifficulty.EXPERT) {
      this.tempo = SongTempo.PRESTO;
      duration = 6000f;
      randomPlayerOrder = true;
    } else {
      throw new IllegalArgumentException("Invalid game difficulty.");
    }

    final float millisPerBeat = (1f / this.tempo.getTempo()) * MILLIS_PER_MINUTE;
    final Map<NoteType, Float> millisPerNote = new HashMap<>();
    for (final NoteType note : NoteType.values()) {
      millisPerNote.put(note, millisPerBeat * note.getBeats());
    }

    // Create notes
    int playerIndex = 0;
    ArrayList<String> playersAvailable = new ArrayList<>(playerNames);
    final Random rand = new Random();
    for (float currentDuration = 0f; currentDuration < duration; ) {
      final NoteType noteType = NoteType.values()[rand.nextInt(NoteType.values().length)];
      final NoteSound noteSound = NoteSound.MAIN;
      final NoteFrequency noteFrequency = NoteFrequency.C4;

      String playerAssignment;
      if (randomPlayerOrder) {
        if (playersAvailable.size() == 0) {
          playersAvailable = new ArrayList<>(playerNames);
        }
        playerIndex = rand.nextInt(playersAvailable.size());
        playerAssignment = playersAvailable.remove(playerIndex);
      } else {
        if (playerIndex >= playersAvailable.size()) {
          playerIndex = 0;
        }
        playerAssignment = playersAvailable.get(playerIndex++);
      }
      this.notes.add(new Note(noteType, noteSound, noteFrequency, playerAssignment));
      currentDuration += millisPerNote.get(noteType);
    }
  }

  public SongTempo getTempo() {
    return this.tempo;
  }

  public List<Note> getNotes() {
    return List.copyOf(this.notes);
  }
}
