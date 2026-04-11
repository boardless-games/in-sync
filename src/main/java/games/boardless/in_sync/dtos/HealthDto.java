package games.boardless.in_sync.dtos;

public class HealthDto {
  private final boolean alive;
  private final String aliveSince;

  public HealthDto(final boolean alive, final String aliveSince) {
    this.alive = alive;
    this.aliveSince = aliveSince;
  }

  public boolean isAlive() {
    return this.alive;
  }

  public String getAliveSince() {
    return this.aliveSince;
  }

  @Override
  public String toString() {
    return "HealthDto [alive=" + alive + ", aliveSince=" + aliveSince + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (alive ? 1231 : 1237);
    result = prime * result + ((aliveSince == null) ? 0 : aliveSince.hashCode());
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
    HealthDto other = (HealthDto) obj;
    if (alive != other.alive)
      return false;
    if (aliveSince == null) {
      if (other.aliveSince != null)
        return false;
    } else if (!aliveSince.equals(other.aliveSince))
      return false;
    return true;
  }
}
