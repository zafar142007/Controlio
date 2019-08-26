package in.controlio;

import java.util.HashSet;
import java.util.Set;

import android.view.View;
import android.widget.ProgressBar;
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
      progressBar.setVisibility(View.GONE);
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
