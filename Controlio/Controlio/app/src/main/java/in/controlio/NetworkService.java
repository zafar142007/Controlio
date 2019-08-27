package in.controlio;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import in.controlio.util.Utility;

public class NetworkService extends IntentService {


  private String host = "192.168.43.238";
  private int port = Utility.PORT;
  private StringBuffer data = null;

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
    //    		MainActivity.socket=new MainActivity.socketket(host, port);
    //    	}catch(Exception e)
    //    	{
    //    		e.printStackTrace();
    //    		System.out.println("Service: I have not been able to open MainActivity.socketket in constructor." + e);
    //    	}
  }

  public void onDestroy() {

  }

  protected void onHandleIntent(Intent workIntent) {
    // Gets data from the incoming Intent
    System.out.println("service: I have received Intent");


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
    } catch (Exception e) {
      e.printStackTrace();
    }
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
      if (MainActivity.socket == null || MainActivity.socket.isClosed() || !MainActivity.socket.isConnected() || !MainActivity.socket.isBound()) {
        if (MainActivity.socket != null) {
          MainActivity.socket.close();
        }
        MainActivity.socket = new Socket(host, port);
        MainActivity.socket.setKeepAlive(true);
        MainActivity.pw = new PrintWriter(new OutputStreamWriter(MainActivity.socket.getOutputStream()), true);
        System.out.println("Service: I have opened a new socket");
      }
      if (MainActivity.pw == null || MainActivity.pw.checkError()) {
        if (MainActivity.pw != null) {
          MainActivity.pw.close();
        }
        MainActivity.pw = new PrintWriter(new OutputStreamWriter(MainActivity.socket.getOutputStream()), true);
      }
      MainActivity.pw.println(message);
      System.out.println("Service:I have written "+message+" data to network");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}