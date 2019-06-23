package in.server;

import in.control.CommandNotFoundException;
import in.control.ControlDelegator;
import in.control.ControlDelegatorImpl;
import in.server.util.ControlioConstants;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;

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

    try {
      int bytes = 0;
      bindChannel();

      while (true) {
        if (!channel.isOpen()) {
          bindChannel();
        }
        System.out.println("Server: I am waiting for a connection");
        SocketChannel ch = channel.accept();
        System.out.println("Server: Accepted socket.");
        bytes = 0;
        ByteBuffer buf = ByteBuffer.allocate(ControlioConstants.BUFFER_SIZE_BYTES);
        while (bytes == 0 && channel.isOpen() && ch.isConnected()) {
          bytes = ch.read(buf);
          if (bytes > 0) {
            byte[] b = buf.array();
            String command = new String(b);
            buf.clear();
            System.out.println("Client: " + command);
            try {
              delegator.delegate(command).execute(command);
            } catch (CommandNotFoundException e) {
              System.out.println("Server: Invalid command: " + e);
            }
            break;
          } else if(bytes==-1){
            channel.close();
            bindChannel();
          }
        }
      }
    } catch (ClosedByInterruptException ex) {
      System.out.println("Network Thread: I have been interrupted");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void bindChannel() throws IOException {
    channel = ServerSocketChannel.open();
    InetSocketAddress serverSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), port);
    channel.bind(serverSocketAddress);
  }


  public void cleanup(){
    try {
      channel.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
