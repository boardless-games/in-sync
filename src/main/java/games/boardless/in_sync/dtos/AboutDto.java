package games.boardless.in_sync.dtos;

public class AboutDto {
  private final String name;
  private final String description;
  private final String clientUrl;

  public AboutDto(final String name, final String description, final String clientUrl) {
    this.name = name;
    this.description = description;
    this.clientUrl = clientUrl;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public String getClientUrl() {
    return this.clientUrl;
  }

  @Override
  public String toString() {
    return "AboutDto [name=" + name + ", description=" + description + ", clientUrl=" + clientUrl + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((clientUrl == null) ? 0 : clientUrl.hashCode());
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
    AboutDto other = (AboutDto) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (clientUrl == null) {
      if (other.clientUrl != null)
        return false;
    } else if (!clientUrl.equals(other.clientUrl))
      return false;
    return true;
  }
}
