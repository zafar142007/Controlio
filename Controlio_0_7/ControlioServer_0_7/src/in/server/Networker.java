package in.server;
import in.control.Command;
import in.control.CommandNotFoundException;
import in.control.ControlScreen;
import in.control.Dictionary;

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
	private int port=8079;
	private ControlScreen remote=new ControlScreen();
	private Dictionary dictionary=new Dictionary();
	private	String host="localhost";

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

	public void setRemote(ControlScreen remote)
	{
		this.remote=remote;

	}
	public String sanitize(String string)
	{
		return string.replace(' ', '_');
	}

	public Command interpret(String command) throws CommandNotFoundException
	{
		Command[] commands=null;

		commands=dictionary.getCommands(sanitize(command));
		if(System.getProperty("os.name").toLowerCase().indexOf("win")>=0)
		{
			for(int i=0;i<commands.length;i++)
				if(commands[i].getPlatform().toString().equals("win"))				
					return commands[i];
		}
		else
			if(System.getProperty("os.name").toLowerCase().indexOf("nux")>=0)
			{
				for(int i=0;i<commands.length;i++)
					if(commands[i].getPlatform().toString().equals("nix"))				
						return commands[i];					
			}

		return null;

	}


	public void run() {

		// TODO Auto-generated method stub
		try{
			InetSocketAddress serverSocket;
			int bytes=0;
			channel=ServerSocketChannel.open();
			serverSocket=new InetSocketAddress(InetAddress.getLocalHost(),port);
			channel.bind(serverSocket);
			
			while(true)
			{
				System.out.println("Server: I am waiting for a connection");
				SocketChannel ch=channel.accept();
				System.out.println("Server: Accepted socket.");
				bytes=0;
				ByteBuffer buf = ByteBuffer.allocate(48);
				while(bytes==0)
				{
					bytes= ch.read(buf);
					if(bytes>0)
					{
						byte[] b=buf.array();

						String command=new String(b);
						buf.clear();
						System.out.println("Client: "+command);
						try{
							remote.execute(interpret(command));
						}
						catch(CommandNotFoundException e)
						{
							System.out.println("Server: Invalid command: "+e);
						}
						System.out.println("Network Thread: Closing channel");
						
						//channel.socket().close();
						//channel.close();
						//ch.socket().close();
						//ch.close();
						
						break;
					}
				}								
			}
		}
		catch(ClosedByInterruptException ex)
		{
			//ex.printStackTrace();
			System.out.println("Network Thread: I have been interrupted");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
