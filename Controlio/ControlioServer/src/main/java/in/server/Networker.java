package in.server;

import in.control.CommandNotFoundException;
import in.control.ControlDelegator;
import in.control.ControlDelegatorImpl;
import in.server.util.ControlioConstants;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Networker implements Runnable {

  //This thread will be started in ControlioServer and would be restarted if ControlioServer's
  //setPort() is called
  private ServerSocketChannel channel;
  private int port = 8079;
  private ControlDelegator delegator = new ControlDelegatorImpl();
  private String host = "localhost";

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }


  public void run() {

    int bytes = 0;
    SocketChannel ch = null;
    ByteBuffer buf;

    while (true) {
      try {
        if (bytes==-1 || channel==null || (channel != null && !channel.isOpen()) || (ch==null) || (ch!=null && !ch.isConnected())) {
          if(channel==null || (channel != null && !channel.isOpen())) {
            bindChannel();
          }
          System.out.println("Server: I am waiting for a connection");
          ch = channel.accept();
          System.out.println("Server: Accepted socket.");
        }
        buf=ByteBuffer.allocate(ControlioConstants.BUFFER_SIZE_BYTES);
        bytes = ch.read(buf);
        if (bytes > 0) {
          byte[] b = buf.array();
          String command = new String(b);
          System.out.println("Client: " + command);
          try {
            delegator.delegate(command).execute(command);
          } catch (CommandNotFoundException e) {
            System.out.println("Server: Invalid command: " + e);
          }
        }
      } catch (ClosedByInterruptException ex) {
        System.out.println("Network Thread: I have been interrupted");
        break;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void bindChannel() throws IOException {
    System.out.println("binding channel");
    channel = ServerSocketChannel.open();
    InetSocketAddress serverSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), port);
    channel.bind(serverSocketAddress);
  }


  public void cleanup() {
    try {
      channel.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
