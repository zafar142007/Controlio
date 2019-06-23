package in.controlio;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import in.controlio.util.Utility;

public class NetworkService extends IntentService {


  private String host = "192.168.43.238";
  private final String BROADCAST_ACTION = "com.example.controlio.BROADCAST";
  private final String SUCCESS = "com.example.controlio.SEND_SUCCESS_STATUS";
  private final String FAILURE = "com.example.controlio.SEND_FAILURE_STATUS";

  private int port = Utility.PORT;
  private StringBuffer data = null;
  private Socket soc = null;
  PrintWriter pw = null;

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

  public NetworkService() {
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

  public void onDestroy() {

    try {
//			ch.close();
      if (soc != null) {
        soc.close();
      }
      System.out.println("Socket: I am being closed");
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Socket: I could not be closed. " + e);
    }

  }

  protected void onHandleIntent(Intent workIntent) {
    // Gets data from the incoming Intent
    System.out.println("service: I have received Intent");
    Intent localIntent;

    String dataString = workIntent.getStringExtra("command");
    String ip = workIntent.getStringExtra("ipaddress");
    String p = workIntent.getStringExtra("port");
    boolean typingMode = workIntent.getBooleanExtra(Utility.TYPE_MODE, false);
    if(typingMode){
      dataString=makeTypeable(dataString);
    } else if(isCopyCommand(dataString)){
      String clipboardContent=getClipboardContent();
      if(clipboardContent==null || clipboardContent.isEmpty()){
        System.out.println("There is no valid text on the clipboard");
        return;
      } else{
        dataString=clipboardContent;
      }
    }

    if (!ip.equals("")) {
      setHost(ip);
    }

    if (!p.equals("")) {
      setPort(Integer.parseInt(p));
    }
    try {
      write(dataString);
      localIntent =
          new Intent(BROADCAST_ACTION)
              // Puts the status into the Intent
              .putExtra("message", SUCCESS);

    } catch (Exception e) {
      e.printStackTrace();
      //call
      localIntent =
          new Intent(BROADCAST_ACTION)
              // Puts the status into the Intent
              .putExtra("message", FAILURE);
      System.out.println("Service: I have not been able to write to network. " + e);
    }
    // Broadcasts the Intent to receivers in this app.
    //  LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    // Do work here, based on the contents of dataString
  }

  private String getClipboardContent() {
    ClipboardManager clipboard = (ClipboardManager)
        getSystemService(Context.CLIPBOARD_SERVICE);
    if (clipboard.hasPrimaryClip()) {
      if (clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
        ClipData data = clipboard.getPrimaryClip();
        if(data!=null){
          String pasteData=data.getItemAt(0).getText().toString();
          return Utility.COPY_COMMAND.concat(" ").concat(pasteData);
        }
      }
    }
    return null;
  }
  private boolean isCopyCommand(String command) {
    return command.equals(Utility.COPY_COMMAND);
  }

  private String makeTypeable(String dataString) {
    if(!dataString.startsWith(Utility.TYPE_PREFIX)){
      return Utility.TYPE_PREFIX.concat(dataString);
    } else return dataString;
  }

  public void write(String message) throws Exception {
    try {
      if (soc == null || soc.isClosed() || !soc.isConnected() || !soc.isBound()) {
        if (soc != null) {
          soc.close();
        }
        soc = new Socket(host, port);
        soc.setKeepAlive(true);
        pw = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()), true);
        System.out.println("Service: I have opened a new Socket");
      }
      if (pw == null || pw.checkError()) {
        if (pw != null) {
          pw.close();
        }
        pw = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()), true);
      }
      pw.println(message);
      System.out.println("Service:I have written data to network");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}