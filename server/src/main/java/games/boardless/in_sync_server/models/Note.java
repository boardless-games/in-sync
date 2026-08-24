package games.boardless.in_sync_server.models;

import games.boardless.in_sync_server.constants.NoteFrequency;
import games.boardless.in_sync_server.constants.NoteLength;
import games.boardless.in_sync_server.constants.NoteSound;

public class Note {
  private final NoteSound sound;
  private final NoteFrequency frequency;
  private final NoteLength length;
  private final Float volume;
  private final int schedule;
  private String playerAssignment;

  public Note(
      final NoteSound sound,
      final NoteFrequency frequency,
      final NoteLength length,
      final Float volume,
      final int schedule) {
    this(sound, frequency, length, volume, schedule, null);
  }

  public Note(
      final NoteSound sound,
      final NoteFrequency frequency,
      final NoteLength length,
      final Float volume,
      final int schedule,
      final String playerAssignment) {
    this.sound = sound;
    this.frequency = frequency;
    this.length = length;
    this.volume = volume;
    this.schedule = schedule;
    this.playerAssignment = playerAssignment;
  }

  public NoteSound getSound() {
    return this.sound;
  }

  public NoteLength getLength() {
    return this.length;
  }

  public NoteFrequency getFrequency() {
    return this.frequency;
  }

  public Float getVolumne() {
    return this.volume;
  }

  public int getSchedule() {
    return this.schedule;
  }

  public String getPlayerAssignment() {
    return this.playerAssignment;
  }

  public void setPlayerAssignment(final String playerAssignment) {
    this.playerAssignment = playerAssignment;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((sound == null) ? 0 : sound.hashCode());
    result = prime * result + ((frequency == null) ? 0 : frequency.hashCode());
    result = prime * result + ((length == null) ? 0 : length.hashCode());
    result = prime * result + ((volume == null) ? 0 : volume.hashCode());
    result = prime * result + schedule;
    result = prime * result + ((playerAssignment == null) ? 0 : playerAssignment.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Note other = (Note) obj;
    if (sound != other.sound)
      return false;
    if (frequency != other.frequency)
      return false;
    if (length != other.length)
      return false;
    if (volume == null) {
      if (other.volume != null)
        return false;
    } else if (!volume.equals(other.volume))
      return false;
    if (schedule != other.schedule)
      return false;
    if (playerAssignment == null) {
      if (other.playerAssignment != null)
        return false;
    } else if (!playerAssignment.equals(other.playerAssignment))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Note [sound=" + sound + ", frequency=" + frequency + ", length=" + length + ", volume=" + volume
        + ", schedule=" + schedule + ", playerAssignment=" + playerAssignment + "]";
  }

  
}
