package games.boardless.in_sync_server.models;

import games.boardless.in_sync_server.constants.Color;
import games.boardless.in_sync_server.constants.NoteSound;

public class SoundBoardButton {
  private final NoteSound sound;
  private final Color color;
  private final String label;

  public SoundBoardButton(final NoteSound sound, final Color color, final String label) {
    this.sound = sound;
    this.color = color;
    this.label = label;
  }

  public NoteSound getSound() {
    return this.sound;
  }

  public Color getColor() {
    return this.color;
  }

  public String getLabel() {
    return this.label;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((sound == null) ? 0 : sound.hashCode());
    result = prime * result + ((color == null) ? 0 : color.hashCode());
    result = prime * result + ((label == null) ? 0 : label.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SoundBoardButton other = (SoundBoardButton) obj;
    if (sound != other.sound) return false;
    if (color != other.color) return false;
    if (label == null) {
      if (other.label != null) return false;
    } else if (!label.equals(other.label)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "SoundBoardButton [sound=" + sound + ", color=" + color + ", label=" + label + "]";
  }
}
