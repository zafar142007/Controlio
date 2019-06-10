package in.control;

public class CommandNotFoundException extends Exception {

  private String message = "Command not found in dictionary!";

  public String toString() {
    return message;
  }

  public String getMessage() {
    return message;
  }

}
