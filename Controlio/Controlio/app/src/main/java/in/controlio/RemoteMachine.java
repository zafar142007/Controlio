package in.controlio;

import java.net.Socket;

public class RemoteMachine {
  private Socket socket;
  private String hostname;

  public RemoteMachine(Socket socket, String hostname) {
    this.socket = socket;
    this.hostname = hostname;
  }

  public Socket getSocket() {
    return socket;
  }

  public String getHostname() {
    return hostname;
  }
}
