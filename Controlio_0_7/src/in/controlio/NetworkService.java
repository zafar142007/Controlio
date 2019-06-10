package in.controlio;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.app.IntentService;
import android.content.Intent;

public class NetworkService extends IntentService {
    
	
	private String host="192.168.43.238";
	private final String BROADCAST_ACTION="com.example.controlio.BROADCAST";
	private final String SUCCESS="com.example.controlio.SEND_SUCCESS_STATUS";
	private final String FAILURE="com.example.controlio.SEND_FAILURE_STATUS";
	private SocketChannel ch;
	//private String host="192.168.42.227";
	
	private int port=8079;
	private StringBuffer data=null;
	private Socket soc=null;
	PrintWriter pw=null;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public NetworkService()
    {
    	super("network");
    	System.out.println("Service: I have been instantiated");
	//    	try{
	//    		soc=new Socket(host, port);
	//    	}catch(Exception e)
	//    	{
	//    		e.printStackTrace();
	//    		System.out.println("Service: I have not been able to open socket in constructor." + e);
	//    	}
    }
	public void onDestroy()
	{
		
		try{
//			ch.close();
			soc.close();
			System.out.println("Socket: I am being closed");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Socket: I could not be closed. "+e);
		}
		
	}
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
    	System.out.println("service: I have received Intent");
    	Intent localIntent; 
    	
    	String dataString = workIntent.getStringExtra("command");
       	String ip = workIntent.getStringExtra("ipaddress");
       	String p = workIntent.getStringExtra("port");
       	
       	if(!ip.equals(""))
       		setHost(ip);
       
       	if(!p.equals(""))
       		setPort(Integer.parseInt(p));
    	try{
			if(soc==null)
			{
//				ch=SocketChannel.open();
//				//ch.bind(new InetSocketAddress("localhost", 8077));
//				InetSocketAddress address=new InetSocketAddress(host, port);
//				System.out.println("Channel is "+ch+", address is "+address);
//				System.out.println("Am I connected? "+ch.isConnected());
//				System.out.print("Service: Have I connected? ");
//				System.out.println(ch.connect(address));
				soc=new Socket(host, port);
				System.out.println("Service: I have opened a new Socket");
			}
			write(dataString);
			localIntent =
		            new Intent(BROADCAST_ACTION)
		            // Puts the status into the Intent
		            .putExtra("message",SUCCESS);
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			Toast t = Toast.makeText(getApplicationContext(),
//					e.getMessage(),
//					Toast.LENGTH_LONG);
//			t.show();
			//call
			localIntent =
		            new Intent(BROADCAST_ACTION)
		            // Puts the status into the Intent
		            .putExtra("message",FAILURE);
		    System.out.println("Service: I have not been able to write to network. "+ e);
		}
		// Broadcasts the Intent to receivers in this app.
	  //  LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

        // Do work here, based on the contents of dataString        
    }
	public void write(String message) throws Exception
	{
//		ByteBuffer buf=ByteBuffer.allocate(48);
//		buf.clear();
//		buf.put(message.getBytes());
//		buf.flip();
//		while(buf.hasRemaining()) {
//			ch.write(buf);
//		}
//		ch.close();
			if(pw==null)
				pw=new PrintWriter(new OutputStreamWriter(soc.getOutputStream()), true);
			pw.println(message);
			System.out.println("Service:I have written data to network");
		
	}

}