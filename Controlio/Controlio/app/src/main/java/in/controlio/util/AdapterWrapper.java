package in.controlio.util;

import java.util.HashSet;
import java.util.Set;

import android.widget.ArrayAdapter;

public class AdapterWrapper {

  private volatile ArrayAdapter<String> hostsAdapter;
  private Set<String> hosts = new HashSet<>();

  public AdapterWrapper(ArrayAdapter<String> hostsAdapter) {
    this.hostsAdapter = hostsAdapter;
  }

  public int size() {
    int size = hosts.size();
    if (hosts.contains("Not connected")){
      size--;
    }
    return size;
  }

  public void addHost(String host, boolean clearExisting) {
    if(clearExisting) {
      hosts.clear();
      hostsAdapter.clear();
    }
    if (!hosts.contains(host)) {
      hosts.add(host);
      hostsAdapter.add(host);
    }
  }

  public ArrayAdapter<String> getHostsAdapter() {
    return hostsAdapter;
  }
}
