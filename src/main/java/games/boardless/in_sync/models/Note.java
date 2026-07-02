package games.boardless.in_sync.models;

import games.boardless.in_sync.constants.NoteType;

public class Note {
  private final NoteType type;
  private final float frequency;
  private final int duration;

  public Note(final NoteType type, final float frequency, final int duration) {
    this.type = type;
    this.frequency = frequency;
    this.duration = duration;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + Float.floatToIntBits(frequency);
    result = prime * result + duration;
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
    if (type != other.type)
      return false;
    if (Float.floatToIntBits(frequency) != Float.floatToIntBits(other.frequency))
      return false;
    if (duration != other.duration)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Note [type=" + type + ", frequency=" + frequency + ", duration=" + duration + "]";
  }
}
