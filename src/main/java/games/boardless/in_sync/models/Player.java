package games.boardless.in_sync.models;

public class Player {
  private final String name;
  private boolean connected;

  public Player(final String name) {
    this.name = name;
    this.connected = false;
  }

  public String getName() {
    return this.name;
  }

  public void setConnected(boolean connected) {
    this.connected = connected;
  }

  public boolean isConnected() {
    return this.connected;
  }

  @Override
  public String toString() {
    return "Player [name=" + name + ", connected=" + connected + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    Player other = (Player) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
}
