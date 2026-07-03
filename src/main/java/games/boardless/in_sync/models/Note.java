package games.boardless.in_sync.models;

import games.boardless.in_sync.constants.NoteDuration;
import games.boardless.in_sync.constants.NoteFrequency;
import games.boardless.in_sync.constants.NoteSound;

public class Note {
  private final NoteSound sound;
  private final NoteDuration duration;
  private final NoteFrequency frequency;

  public Note(final NoteSound sound, final NoteDuration duration, final NoteFrequency frequency) {
    this.sound = sound;
    this.duration = duration;
    this.frequency = frequency;
  }

  public NoteSound getSound() {
    return this.sound;
  }

  public NoteDuration getDuration() {
    return this.duration;
  }

  public NoteFrequency getFrequency() {
    return this.frequency;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((sound == null) ? 0 : sound.hashCode());
    result = prime * result + ((frequency == null) ? 0 : frequency.hashCode());
    result = prime * result + ((duration == null) ? 0 : duration.hashCode());
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
    if (duration != other.duration)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Note [type=" + sound + ", frequency=" + frequency + ", duration=" + duration + "]";
  }
}
