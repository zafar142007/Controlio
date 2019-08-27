package in.controlio;

import java.util.HashSet;
import java.util.Set;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import in.controlio.util.AdapterWrapper;

public class UpdateAdapterJob implements Runnable{

  private final ProgressBar progressBar;
  private Integer numberOfHosts;

  public UpdateAdapterJob(Integer numberOfHosts, ProgressBar progressBar) {
    this.progressBar=progressBar;
    this.numberOfHosts=numberOfHosts;
  }

  @Override
  public void run() {
    try {
      Toast.makeText(progressBar.getContext(),
          "Scanning done -- found "+numberOfHosts+" connectable devices on your internal network",
          Toast.LENGTH_LONG).show();

      progressBar.setVisibility(View.GONE);
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
