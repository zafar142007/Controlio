package in.controlio;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import in.controlio.util.AdapterWrapper;
import in.controlio.util.Utility;

public class MainActivity extends Activity {

  protected static final int RESULT_SPEECH = 1;
  public static Socket socket = null;
  public static PrintWriter pw = null;
  private ImageButton btnSpeak;
  private MenuItem hostIPTextbox;
  private ProgressBar progressBar;
  private Button send;
  private Button copyButton;
  private EditText hostPort;
  private Switch typingMode;
  private NetworkScanner scanner = new NetworkScanner(Utility.PORT);
  private Button tab, closeTab, activeWindows, newTab, previousTab, nextTab, pageUp, pageDown,
      back, forward, up, reload, left, down, right, options, enter, space, scanButton;
  private Spinner hostsDropdown;
  private AdapterWrapper adapterWrapper;
  private MyListener myListener = new MyListener();

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adapterWrapper = new AdapterWrapper(new ArrayAdapter<String>(
        this, android.R.layout.simple_spinner_dropdown_item));
    setContentView(R.layout.activity_main);
    progressBar = findViewById(R.id.progressBar);
    hostsDropdown = findViewById(R.id.hostsDropdown);
    copyButton = findViewById(R.id.copyButton);
    typingMode = findViewById(R.id.typingMode);
    send = (Button) findViewById(R.id.send);
    tab = (Button) findViewById(R.id.next);
    closeTab = (Button) findViewById(R.id.closeTab);
    activeWindows = (Button) findViewById(R.id.activeWindows);
    newTab = (Button) findViewById(R.id.newTab);
    previousTab = (Button) findViewById(R.id.previousTab);
    nextTab = (Button) findViewById(R.id.nextTab);
    pageUp = (Button) findViewById(R.id.pageUp);
    pageDown = (Button) findViewById(R.id.pageDown);
    back = (Button) findViewById(R.id.back);
    forward = (Button) findViewById(R.id.forward);
    up = (Button) findViewById(R.id.up);
    reload = (Button) findViewById(R.id.reload);
    left = (Button) findViewById(R.id.left);
    down = (Button) findViewById(R.id.down);
    right = (Button) findViewById(R.id.right);
    enter = (Button) findViewById(R.id.open);
    options = (Button) findViewById(R.id.options);
    space = (Button) findViewById(R.id.stop);
    scanButton = findViewById(R.id.scanButton);

    hostsDropdown.setAdapter(adapterWrapper.getHostsAdapter());

    hostsDropdown.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String value = parent.getItemAtPosition(position).toString();
        if (!value.equals(Utility.SEARCHING_MESSAGE)) {
          hostIPTextbox.setTitle(value);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    copyButton.setOnClickListener(myListener);
    tab.setOnClickListener(myListener);
    closeTab.setOnClickListener(myListener);
    activeWindows.setOnClickListener(myListener);
    newTab.setOnClickListener(myListener);
    previousTab.setOnClickListener(myListener);
    nextTab.setOnClickListener(myListener);
    pageUp.setOnClickListener(myListener);
    pageDown.setOnClickListener(myListener);
    back.setOnClickListener(myListener);
    forward.setOnClickListener(myListener);
    up.setOnClickListener(myListener);
    reload.setOnClickListener(myListener);
    left.setOnClickListener(myListener);
    down.setOnClickListener(myListener);
    right.setOnClickListener(myListener);
    enter.setOnClickListener(myListener);
    options.setOnClickListener(myListener);
    space.setOnClickListener(myListener);
    scanButton.setOnClickListener(myListener);

    btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

    send.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        System.out.println("Button: I have been clicked");
        Intent mServiceIntent = new Intent(MainActivity.this.getApplicationContext(),
            NetworkService.class);
        //mServiceIntent.setData(Uri.parse(""));
        mServiceIntent.putExtra(Utility.TYPE_MODE, false);
        String ip = "";
        mServiceIntent.putExtra("command", "TEST_COMMAND");
        if (hostIPTextbox.getTitle() != null) {
          ip = hostIPTextbox.getTitle().toString();
        }
        mServiceIntent.putExtra("ipaddress", ip);
        System.out.println("TextBox: ip is " + ip);
        String port = getPort();
        System.out.println("Activity: Port being embedded is " + port);
        mServiceIntent.putExtra("port", port);
        System.out.println("Main: I am starting service");
        MainActivity.this.startService(mServiceIntent);

      }
    });
    btnSpeak.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        Intent intent = new Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en_IN");

        try {
          startActivityForResult(intent, RESULT_SPEECH);
          //txtText.setText("");
        } catch (ActivityNotFoundException a) {
          Toast t = Toast.makeText(getApplicationContext(),
              "Oops! Your device doesn't support Speech to Text",
              Toast.LENGTH_SHORT);
          t.show();
        }
      }
    });

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    hostIPTextbox = menu.findItem(R.id.hostIPTextbox);
    menu.findItem(R.id.about).setOnMenuItemClickListener((item)->{
      LayoutInflater inflater = (LayoutInflater)
          getSystemService(LAYOUT_INFLATER_SERVICE);
      View popupView = inflater.inflate(R.layout.popup_layout, null);

      // create the popup window
      int width = LinearLayout.LayoutParams.WRAP_CONTENT;
      int height = LinearLayout.LayoutParams.WRAP_CONTENT;
      boolean focusable = true; // lets taps outside the popup also dismiss it
      final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
      TextView aboutText=popupView.findViewById(R.id.aboutText);
      aboutText.setMovementMethod(LinkMovementMethod.getInstance());
      // show the popup window
      // which view you pass in doesn't matter, it is only used for the window token
      popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
      return true;
    });
    menu.findItem(R.id.contact).setOnMenuItemClickListener((item)->{
      Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
          "mailto", Utility.email, null));
      intent.putExtra(Intent.EXTRA_SUBJECT, "[Feedback] App Controlio");
      StringBuilder stringBuilder=new StringBuilder();
      DisplayMetrics dm = new DisplayMetrics();
      MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);

      double widthInches =dm.widthPixels / dm.xdpi;
      double heightInches =dm.heightPixels / dm.ydpi;
      stringBuilder
          .append("Manufacturer: ")
          .append( Build.MANUFACTURER)
          .append(", Brand: ")
          .append( Build.BRAND)
          .append(", Model: ")
          .append( Build.MODEL)
          .append(", Board: ")
          .append( Build.BOARD)
          .append(", Hardware: ")
          .append( Build.HARDWARE)
          .append(", Device: ")
          .append( Build.DEVICE)
          .append(", Manufacturer: ")
          .append( Build.MANUFACTURER)
          .append(", Version: ")
          .append( Build.VERSION.RELEASE)
          .append(", SDK: ")
          .append( Build.VERSION.SDK_INT)
          .append(", Width in inches: ")
          .append(widthInches)
          .append(", Height in inches: ")
          .append( heightInches)
          .append('\n')
      .append("-----------------------------------------------------------")
      .append("Please type below this line")
          .append('\n')
      .append("-----------------------------------------------------------");

      intent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
      startActivity(Intent.createChooser(intent, "Choose an Email client"));
      return true;
    });


    return true;
  }

  public void sendToService(String text) {
    Intent mServiceIntent = new Intent(MainActivity.this.getApplicationContext(),
        NetworkService.class);
    if (hostIPTextbox != null && hostIPTextbox.getTitle() != null && !hostIPTextbox.getTitle()
        .toString().isEmpty() && !hostIPTextbox.getTitle()
        .toString().equals("Not connected")) {
      String ip = hostIPTextbox.getTitle().toString();
      mServiceIntent.putExtra("ipaddress", ip);
      mServiceIntent.putExtra(Utility.TYPE_MODE, false);

      String port = getPort();
      System.out.println("Activity: Port being embedded is " + port);
      mServiceIntent.putExtra("port", port);

      System.out.println("TextBox: ip is " + ip);
      mServiceIntent.putExtra("command", text);
      System.out.println("Main: I am starting service");
      MainActivity.this.startService(mServiceIntent);
    } else {
      Toast.makeText(getApplicationContext(),
          "Not connected to any device! ",
          Toast.LENGTH_LONG).show();
    }
  }

  private String getPort() {
    return String.valueOf(Utility.PORT);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case RESULT_SPEECH: {
        if (resultCode == RESULT_OK && null != data) {

          ArrayList<String> text = data
              .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          Toast.makeText(getApplicationContext(),
              "You spoke: " + text.get(0),
              Toast.LENGTH_LONG).show();

          System.out.println("STT Button: I have been clicked");
          Intent mServiceIntent = new Intent(MainActivity.this.getApplicationContext(),
              NetworkService.class);
          if (hostIPTextbox != null && hostIPTextbox.getTitle() != null && !hostIPTextbox.getTitle()
              .toString().isEmpty() && !hostIPTextbox.getTitle().toString().equals("Not connected")) {
            String ip = hostIPTextbox.getTitle().toString();
            mServiceIntent.putExtra("ipaddress", ip);
            mServiceIntent.putExtra(Utility.TYPE_MODE, typingMode.isChecked());
            String port = getPort();
            System.out.println("Activity: Port being embedded is " + port);
            mServiceIntent.putExtra("port", port);

            System.out.println("TextBox: ip is " + ip);
            mServiceIntent.putExtra("command", text.get(0));
            System.out.println("Main: I am starting service");
            MainActivity.this.startService(mServiceIntent);
          } else {
            Toast.makeText(getApplicationContext(),
                "Not connected to any device! ",
                Toast.LENGTH_LONG).show();
          }
        }
        break;
      }

    }
  }

  private class MyListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      System.out.println("Keyboard: Button clicked " + v);
      if (v.equals(scanButton)) {
        Toast.makeText(MainActivity.this,
            "Scanning your internal network for connectable devices. This may take a few minutes.",
            Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.VISIBLE);
        scanner.scan(adapterWrapper, progressBar, MainActivity.this);
      } else if (v.equals(tab)) {
        sendToService("next");
      } else if (v.equals(closeTab)) {
        sendToService("close tab");

      } else if (v.equals(activeWindows)) {
        sendToService("active windows");
      } else if (v.equals(newTab)) {
        sendToService("new tab");
      } else if (v.equals(previousTab)) {
        sendToService("previous tab");
      } else if (v.equals(nextTab)) {
        sendToService("next tab");
      } else if (v.equals(pageUp)) {
        sendToService("page up");
      } else if (v.equals(pageDown)) {
        sendToService("page down");
      } else if (v.equals(back)) {
        sendToService("back");
      } else if (v.equals(forward)) {
        sendToService("forward");
      } else if (v.equals(reload)) {
        sendToService("reload");
      } else if (v.equals(up)) {
        sendToService("up");
      } else if (v.equals(down)) {
        sendToService("down");
      } else if (v.equals(left)) {
        sendToService("left");
      } else if (v.equals(right)) {
        sendToService("right");
      } else if (v.equals(options)) {
        sendToService("window options");
      } else if (v.equals(enter)) {
        sendToService("open");
      } else if (v.equals(space)) {
        sendToService("stop");
      } else if (v.equals(copyButton)) {
        sendToService(Utility.COPY_COMMAND);
      }
    }

  }
}
