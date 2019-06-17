package in.control;

public class Command {


  private Platform platform;
  private String payload;

  public Command() {
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  public Platform getPlatform() {
    return platform;
  }

  public String getPayload() {
    return payload;
  }
}
