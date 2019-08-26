package in.controlio;

import java.util.HashSet;
import java.util.Set;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import in.controlio.util.AdapterWrapper;

public class UpdateAdapterJob implements Runnable{

  private final ProgressBar progressBar;
  Set<String> hosts=new HashSet<>();
  AdapterWrapper adapter;

  public UpdateAdapterJob(Set<String> hosts, AdapterWrapper hostsAdapter, ProgressBar progressBar) {
    this.hosts=hosts;
    this.adapter=hostsAdapter;
    this.progressBar=progressBar;
  }

  @Override
  public void run() {
    try {
      for (String host : hosts) {
        adapter.addHost(host);
      }
      Toast.makeText(progressBar.getContext(),
          "Scanning done -- found "+hosts.size()+" connectable devices on your internal network",
          Toast.LENGTH_LONG).show();

      progressBar.setVisibility(View.GONE);
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
