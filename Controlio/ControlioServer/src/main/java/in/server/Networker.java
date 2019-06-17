package in.server;

import in.control.Command;
import in.control.CommandNotFoundException;
import in.control.ControlDelegator;
import in.control.ControlDelegatorImpl;
import in.control.ControlScreen;
import in.control.Dictionary;
import in.server.util.ControlioConstants;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Networker implements Runnable {

  //This thread will be started in ControlioServer and would be restarted if ControlioServer's
  //setPort() is called
  private ServerSocketChannel channel;
  private int port = 8079;
  private ControlDelegator delegator=new ControlDelegatorImpl() ;
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
        while (bytes == 0) {
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
          }
        }
      }
    } catch (ClosedByInterruptException ex) {
      //ex.printStackTrace();
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

}
