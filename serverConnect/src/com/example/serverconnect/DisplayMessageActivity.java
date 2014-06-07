package com.example.serverconnect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class DisplayMessageActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);
		// Show the Up button in the action bar.
		setupActionBar();
		
		ServerConnectionFactory fac = new ServerConnectionFactory(1);
		ServerConnection s = fac.getConnection();
		Map<String, String> m = new HashMap<String, String>();
		m.put("user_id","abcdefg");
		Map<String, Pair<String,InputStream>> params = new HashMap<String, Pair<String,InputStream>>();	
		String message = "";
		try {
			long t0 = System.currentTimeMillis();
			params.put("picture",new Pair<String,InputStream>("ll.mp3", getAssets().open("l.mp3")));
			Future<JSONObject> f1 = s.asyncSendPOSTRequest("http://www.doc.ic.ac.uk/project/2013/271/g1327111/rishabh's%20testing/testing.php", m, params);
			message = f1.get().toString();
			System.out.println("upload time :" + (System.currentTimeMillis() - t0));
			
			long t1 = System.currentTimeMillis();
			PreciousFile file = MainActivity.cm.getFile("ndshfbsdie.mp3", 10);
			s.getFileInputStream("http://www.doc.ic.ac.uk/project/2013/271/g1327111/rishabh's%20testing/uploads/ll.mp3", file);
			System.out.println("download time :" + (System.currentTimeMillis() - t1));
//			InputStream in = file.getInputStream();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//			StringBuilder stringReply = new StringBuilder();
//			String replyLine;
//			while ((replyLine = reader.readLine()) != null) {
//			    stringReply.append(replyLine);
//			}
//			System.out.println(stringReply);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			message = "  a  " + e.toString();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message = "  b  " + e.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			message = "  c  " + e.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			message = " d  " + e.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			message = " e  " + e.toString();
		}
		fac.recycleConnection(s);
		fac.destroyAllConnection();
	    TextView textView = (TextView) findViewById(R.id.view_text);
	    textView.setText(message);


	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
