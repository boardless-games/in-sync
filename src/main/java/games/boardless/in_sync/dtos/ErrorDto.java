package games.boardless.in_sync.dtos;

public class ErrorDto {
  private String error;

  public ErrorDto(final String error) {
    this.error = error;
  }

  public String getError() {
    return this.error;
  }
}
