package in.controlio;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import in.controlio.util.Utility;

public class MainActivity extends Activity {

	protected static final int RESULT_SPEECH = 1;
	private ImageButton btnSpeak;
	private TextView txtText;
	private EditText hostIPTextbox;
	private Button send;
	private Button copyButton;
	private EditText hostPort;
	private Switch typingMode;
	private Button tab, closeTab, activeWindows, newTab, previousTab, nextTab, pageUp, pageDown, 
		back, forward, up, reload, left, down, right, options, enter, space;
	
	private class MyListener implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {

			System.out.println("Keyboard: Button clicked "+v);

			if(v.equals(tab))
			{
				sendToService("next");
			}
			else if(v.equals(closeTab))
			{
				sendToService("close tab");
				
			}else if(v.equals(activeWindows))
			{
				sendToService("active windows");
			}else if(v.equals(newTab))
			{
				sendToService("new tab");
			}else if(v.equals(previousTab))
			{
				sendToService("previous tab");
			}else if(v.equals(nextTab))
			{
				sendToService("next tab");
			}else if(v.equals(pageUp))
			{
				sendToService("page up");
			}else if(v.equals(pageDown))
			{
				sendToService("page down");
			}else if(v.equals(back))
			{
				sendToService("back");
			}else if(v.equals(forward))
			{
				sendToService("forward");
			}else if(v.equals(reload))
			{
				sendToService("reload");
			}
			else if(v.equals(up))
			{
				sendToService("up");
			}else if(v.equals(down))
			{
				sendToService("down");
			}else if(v.equals(left))
			{
				sendToService("left");
			}
			else if(v.equals(right))
			{
				sendToService("right");
			}
			else if(v.equals(options))
			{
				sendToService("window options");
			}else if(v.equals(enter))
			{
				sendToService("open");
			}else if(v.equals(space))
			{
				sendToService("stop");
			} else if(v.equals(copyButton)){
				sendToService(Utility.COPY_COMMAND);
			}
		}
		
	}
	private MyListener myListener=new MyListener();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		copyButton=findViewById(R.id.copyButton);
		typingMode=findViewById(R.id.typingMode);
		send=(Button)findViewById(R.id.send);
		tab= (Button) findViewById(R.id.next);
		closeTab= (Button) findViewById(R.id.closeTab);
		activeWindows= (Button) findViewById(R.id.activeWindows);
		newTab= (Button) findViewById(R.id.newTab);
		previousTab= (Button) findViewById(R.id.previousTab);
		nextTab= (Button) findViewById(R.id.nextTab);
		pageUp= (Button) findViewById(R.id.pageUp);
		pageDown= (Button) findViewById(R.id.pageDown);
		back= (Button) findViewById(R.id.back);
		forward= (Button) findViewById(R.id.forward);
		up= (Button) findViewById(R.id.up);
		reload= (Button) findViewById(R.id.reload);
		left= (Button) findViewById(R.id.left);
		down= (Button) findViewById(R.id.down);
		right= (Button) findViewById(R.id.right);
		enter= (Button) findViewById(R.id.open);
		options=(Button) findViewById(R.id.options);
		space= (Button) findViewById(R.id.stop);

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
		
		
		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
		hostIPTextbox=(EditText) findViewById(R.id.hostIPTextbox);
		//hostPort=(EditText) findViewById(R.id.hostPort);
		
		send.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v)
			{
				System.out.println("Button: I have been clicked");
				Intent mServiceIntent = new Intent(MainActivity.this.getApplicationContext(), NetworkService.class);
				//mServiceIntent.setData(Uri.parse(""));
				mServiceIntent.putExtra(Utility.TYPE_MODE, false);
				String ip="";
				mServiceIntent.putExtra("command", "TEST_COMMAND");
				if(hostIPTextbox.getText()!=null)
					ip=hostIPTextbox.getText().toString();
				mServiceIntent.putExtra("ipaddress", ip);
				System.out.println("TextBox: ip is "+ip);
				String port=getPort();
				System.out.println("Activity: Port being embedded is "+port);
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
		return true;
	}

	public void sendToService(String text){
		Intent mServiceIntent = new Intent(MainActivity.this.getApplicationContext(), NetworkService.class);
		String ip="";
		if(hostIPTextbox.getText()!=null)
			ip=hostIPTextbox.getText().toString();
		mServiceIntent.putExtra("ipaddress", ip);
		mServiceIntent.putExtra(Utility.TYPE_MODE, false);

		String port=getPort();
		System.out.println("Activity: Port being embedded is "+port);
		mServiceIntent.putExtra("port", port);
		
		System.out.println("TextBox: ip is "+ip);
		mServiceIntent.putExtra("command", text);
		System.out.println("Main: I am starting service");
		MainActivity.this.startService(mServiceIntent);

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
				Toast t = Toast.makeText(getApplicationContext(),
						"You spoke: "+text.get(0),
						Toast.LENGTH_LONG);
				t.show();

				System.out.println("STT Button: I have been clicked");
				Intent mServiceIntent = new Intent(MainActivity.this.getApplicationContext(), NetworkService.class);
				String ip="";
				if(hostIPTextbox.getText()!=null)
					ip=hostIPTextbox.getText().toString();
				mServiceIntent.putExtra("ipaddress", ip);
				mServiceIntent.putExtra(Utility.TYPE_MODE, typingMode.isChecked());
				String port=getPort();
				System.out.println("Activity: Port being embedded is "+port);
				mServiceIntent.putExtra("port", port);
				
				System.out.println("TextBox: ip is "+ip);
				mServiceIntent.putExtra("command", text.get(0));
				System.out.println("Main: I am starting service");
				MainActivity.this.startService(mServiceIntent);
			}
			break;
		}

		}
	}
}
