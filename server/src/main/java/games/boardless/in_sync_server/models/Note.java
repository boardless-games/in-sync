package games.boardless.in_sync_server.models;

import games.boardless.in_sync_server.constants.NoteFrequency;
import games.boardless.in_sync_server.constants.NoteSound;
import games.boardless.in_sync_server.constants.NoteType;

public class Note {
  private final NoteType type;
  private final NoteSound sound;
  private final NoteFrequency frequency;
  private final int schedule;
  private String playerAssignment;

  public Note(
      final NoteType type,
      final NoteSound sound,
      final NoteFrequency frequency,
      final int schedule,
      final String playerAssignment) {
    this.type = type;
    this.sound = sound;
    this.frequency = frequency;
    this.schedule = schedule;
    this.playerAssignment = playerAssignment;
  }

  public NoteSound getSound() {
    return this.sound;
  }

  public NoteType getType() {
    return this.type;
  }

  public NoteFrequency getFrequency() {
    return this.frequency;
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
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((sound == null) ? 0 : sound.hashCode());
    result = prime * result + ((frequency == null) ? 0 : frequency.hashCode());
    result = prime * result + ((playerAssignment == null) ? 0 : playerAssignment.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Note other = (Note) obj;
    if (type != other.type) return false;
    if (sound != other.sound) return false;
    if (frequency != other.frequency) return false;
    if (playerAssignment == null) {
      if (other.playerAssignment != null) return false;
    } else if (!playerAssignment.equals(other.playerAssignment)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Note [type="
        + type
        + ", sound="
        + sound
        + ", frequency="
        + frequency
        + ", playerAssignment="
        + playerAssignment
        + "]";
  }
}
