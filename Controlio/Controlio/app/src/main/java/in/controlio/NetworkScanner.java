package in.controlio;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import in.controlio.util.Utility;

public class NetworkScanner {

  private int port;

  private ExecutorService executorService= Executors.newFixedThreadPool(1);
  public NetworkScanner(int port) {
    this.port = port;
  }


  public void scan(final ArrayAdapter<String> hostsAdapter, final ProgressBar progressBar){
    executorService.submit(new Runnable() {
      @Override
      public void run() {
        try {
          findSockets(getInterfaceAddresses(), hostsAdapter);
          System.out.println("scanning done");
          progressBar.setVisibility(View.GONE);
        } catch (SocketException e) {
          e.printStackTrace();
        }
      }
    });
  }

  public Enumeration<NetworkInterface> getInterfaceAddresses() throws SocketException {
    return NetworkInterface.getNetworkInterfaces();
  }

  public void findSockets(Enumeration<NetworkInterface> networks,
      ArrayAdapter<String> hostsAdapter) {

    Set<String> set=new HashSet<>();
    for (NetworkInterface network : Collections.list(networks)) {
      List<InterfaceAddress> interfaces= network.getInterfaceAddresses();
      ArrayList<InetAddress> inetAddresses = Collections.list(network.getInetAddresses());
      for (InterfaceAddress interfaceAddress : interfaces) {
        InetAddress inetAddress=interfaceAddress.getAddress();
        if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
          int mask=interfaceAddress.getNetworkPrefixLength();

          Integer add = Integer.parseInt(inetAddress.getHostAddress()
              .substring(inetAddress.getHostAddress().lastIndexOf(".") + 1));
          List<Byte[]> addresses=getUsableAddresses(inetAddress.getAddress(), mask);

          for (Byte[] address:addresses) {
              try {
                RemoteMachine remote;
                if ((remote = checkHost(address)) != null) {

                  if(hostsAdapter.getPosition(remote.getHostname())==-1) {
                    hostsAdapter.add(remote.getHostname());
                  }
                  remote.getSocket().close();
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
          }
        }
      }
    }
  }


  private List<Byte[]> getUsableAddresses(byte[] address, int mask) {
    List<Byte[]> addresses=new ArrayList<>();
    if(address.length==4 && mask<32){
      byte start=0, end=0;

      start= (byte) (address[(int)((mask-1)/8)]&getMaskByte(mask%8));
      end=(byte)(start+Math.pow(2,8-(mask&8))-1);
      addresses=generateByteAddresses(address, start, end, (int)((mask-1)/8));

    }
    return addresses;

  }

  private List<Byte[]> generateByteAddresses(byte[] address, byte start, byte end, int subnetMaskByte) {
    List<Byte[]> addresses=new ArrayList<>();
    Byte[] addr=new Byte[4];
    for(int j=0; j<subnetMaskByte; j++){
      addr[j]=address[j];
    }
    for(byte j=start; j<=end; j++){
      addr[subnetMaskByte]=j;
      generateAllCombinations(addresses,addr, subnetMaskByte+1);
    }
    return addresses;
  }

  private void generateAllCombinations(List<Byte[]> addresses, Byte[] addr, int bytePosition) {
    if(bytePosition>3){
      addresses.add(addr);
      return;
    }else{
      int k=0;
      for(byte b=-128;  k<256; b++, k++){
        Byte[] address=addr.clone();
        address[bytePosition]=b;
        generateAllCombinations(addresses, address, bytePosition+1);
      }
    }
  }

  private byte getMaskByte(int m) {
    byte b=0;
    if(m==0)
      m=8;
    for(int i=7; i>(7-m); i--){
      b+=(byte)Math.pow(2, i);
    }
    return b;
  }

  private RemoteMachine checkHost(Byte[] address) throws IOException {
    byte[] add=new byte[4];
    for(int i=0; i<4; i++){
      add[i]=address[i];
    }
    InetAddress host=InetAddress.getByAddress(add);
    String h=host.getHostName();
    if(host.isReachable(Utility.REACHABILTY_TIMEOUT_MS)){
      Socket soc = new Socket();
      try {
        soc.connect(new InetSocketAddress(host, port), Utility.SCAN_TIMEOUT_MS);
        System.out.println("could connect to "+host);
        return new RemoteMachine(soc, h);
      }catch(Exception e){
        System.out.println("could not connect to "+host);
        return null;
      }
    } else{
      return null;
    }
  }

}
