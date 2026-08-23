package games.boardless.in_sync_server.models;

import games.boardless.in_sync_server.constants.NoteLength;
import games.boardless.in_sync_server.constants.NoteSound;
import games.boardless.in_sync_server.constants.Song;
import games.boardless.in_sync_server.constants.SongTempo;
import games.boardless.in_sync_server.dtos.SongSettingsDto;
import games.boardless.in_sync_server.exceptions.BadRequestException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

public class AssignedSong {
  private static final int MAX_RANDOM_SONG_DURATION = 15_000;

  private static Map<NoteLength, Integer> getMillisPerNoteLength(final SongTempo tempo) {
    final int millisPerBeat = tempo.getMillisPerBeat();

    final Map<NoteLength, Integer> millisPerNote = new HashMap<>();
    for (final NoteLength noteLength : NoteLength.values()) {
      millisPerNote.put(noteLength, (int) (millisPerBeat * noteLength.getBeats()));
    }

    return millisPerNote;
  }

  private static Note[] createRandomSongNotes(
      final String[] playerNames, final Map<NoteLength, Integer> millisPerNoteLength) {
    final Random rand = new Random();

    final NoteLength[] noteLengthValues = NoteLength.values();
    final SoundBoardButton[][] randomSongButtons = Song.RANDOM.getButtons();

    final List<String> playersList = Arrays.asList(playerNames);
    final ArrayList<String> playersAvailable = new ArrayList<>(playersList);

    final int adjustedMaxDuration =
        AssignedSong.MAX_RANDOM_SONG_DURATION - millisPerNoteLength.get(NoteLength.EIGHTH);
    final ArrayList<Note> randomNotes = new ArrayList<>();
    for (int nextSchedule = 0;
        nextSchedule < adjustedMaxDuration;
        nextSchedule +=
            millisPerNoteLength.get(noteLengthValues[rand.nextInt(noteLengthValues.length)])) {
      final int randomButtonRow = rand.nextInt(randomSongButtons.length);
      final NoteSound noteSound =
          randomSongButtons[randomButtonRow][
              rand.nextInt(randomSongButtons[randomButtonRow].length)]
              .getSound();

      if (playersAvailable.size() == 0) {
        playersAvailable.addAll(playersList);
      }

      randomNotes.add(
          new Note(
              noteSound,
              null,
              NoteLength.EIGHTH,
              1,
              nextSchedule,
              playersAvailable.remove(rand.nextInt(playersAvailable.size()))));
    }
    return (Note[]) randomNotes.toArray();
  }

  private String title;
  private SongTempo tempo;
  private int duration;
  private SoundBoardButton[][] buttons;
  private Note[] notes;

  public AssignedSong(final SongSettingsDto settings, final String[] playerNames)
      throws BadRequestException {
    final Map<NoteLength, Integer> millisPerNoteLength =
        AssignedSong.getMillisPerNoteLength(this.tempo);

    this.title = settings.song().getTitle();
    this.tempo = settings.songTempo();
    this.buttons = settings.song().getButtons();
    if (settings.song() != Song.RANDOM) {
      this.notes = settings.song().getNotes();
      // Assign notes
    } else {
      this.notes = AssignedSong.createRandomSongNotes(playerNames, millisPerNoteLength);
    }

    this.duration =
        Stream.of(this.notes)
            .mapToInt((note) -> note.getSchedule() + millisPerNoteLength.get(note.getLength()))
            .max()
            .orElseThrow();
  }

  public String getTitle() {
    return this.title;
  }

  public SongTempo getTempo() {
    return this.tempo;
  }

  public int getDuration() {
    return this.duration;
  }

  public SoundBoardButton[][] getButtons() {
    return this.buttons;
  }

  public Note[] getNotes() {
    return this.notes;
  }
}
