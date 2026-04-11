package games.boardless.in_sync.dtos;

public class ErrorDto {
  private final String error;

  public ErrorDto(final String error) {
    this.error = error;
  }

  public String getError() {
    return this.error;
  }

  @Override
  public String toString() {
    return "ErrorDto [error=" + error + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((error == null) ? 0 : error.hashCode());
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
    ErrorDto other = (ErrorDto) obj;
    if (error == null) {
      if (other.error != null)
        return false;
    } else if (!error.equals(other.error))
      return false;
    return true;
  }
}
