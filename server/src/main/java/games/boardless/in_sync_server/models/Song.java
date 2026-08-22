package games.boardless.in_sync_server.models;

import games.boardless.in_sync_server.constants.NoteLength;
import games.boardless.in_sync_server.constants.NoteSound;
import games.boardless.in_sync_server.constants.SongDuration;
import games.boardless.in_sync_server.constants.SongTempo;
import games.boardless.in_sync_server.constants.SongType;
import games.boardless.in_sync_server.dtos.SongSettingsDto;
import games.boardless.in_sync_server.exceptions.BadRequestException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Song {
  private final SongType type;
  private final SongTempo tempo;
  private final SongDuration duration;
  private final boolean metronome;
  private final boolean randomPlayerOrder;
  private final ArrayList<Note> notes = new ArrayList<>();

  public Song(final SongSettingsDto settings, final String[] playerNames)
      throws BadRequestException {
    this.type = settings.songType();
    this.tempo = settings.songTempo();
    this.duration = settings.songDuration();
    this.metronome = settings.metronome();
    this.randomPlayerOrder = settings.randomPlayerOrder();

    if (this.type == SongType.RANDOM) {
      this.createRandomSong(playerNames);
    } else {
      throw new BadRequestException("Invalid song type.");
    }
  }

  public SongType getType() {
    return this.type;
  }

  public SongTempo getTempo() {
    return this.tempo;
  }

  public SongDuration getDuration() {
    return this.duration;
  }

  public boolean hasMetronome() {
    return this.metronome;
  }

  public boolean isRandomPlayerOrder() {
    return this.randomPlayerOrder;
  }

  public List<Note> getNotes() {
    return List.copyOf(this.notes);
  }

  private <T> ArrayList<T> toArrayList(T[] array) {
    return new ArrayList<>(Arrays.asList(array));
  }

  private Map<NoteLength, Integer> getMillisPerNoteType() {
    final int millisPerBeat = this.tempo.getMillisPerBeat();

    final Map<NoteLength, Integer> millisPerNote = new HashMap<>();
    for (final NoteLength noteType : NoteLength.values()) {
      millisPerNote.put(noteType, (int) (millisPerBeat * noteType.getBeats()));
    }

    return millisPerNote;
  }

  private void createRandomSong(final String[] playerNames) {
    final Map<NoteLength, Integer> millisPerNoteType = this.getMillisPerNoteType();
    final Random rand = new Random();
    final int songDuration = this.duration.getDuration();

    int playerIndex = 0;
    ArrayList<String> playersAvailable = this.toArrayList(playerNames);

    for (int currentDuration = 0; currentDuration < songDuration; ) {
      final NoteLength noteLength = NoteLength.values()[rand.nextInt(NoteLength.values().length)];
      final NoteSound noteSound = SongType.RANDOM.getAvailableSounds() 

      String playerAssignment;
      if (this.randomPlayerOrder) {
        if (playersAvailable.size() == 0) {
          playersAvailable = this.toArrayList(playerNames);
        }
        playerAssignment = playersAvailable.remove(rand.nextInt(playersAvailable.size()));
      } else {
        if (playerIndex >= playersAvailable.size()) {
          playerIndex = 0;
        }
        playerAssignment = playersAvailable.get(playerIndex++);
      }

      this.notes.add(new Note(noteSound, null, noteLength, currentDuration, playerAssignment));

      currentDuration += millisPerNoteType.get(noteLength);
    }
  }
}
