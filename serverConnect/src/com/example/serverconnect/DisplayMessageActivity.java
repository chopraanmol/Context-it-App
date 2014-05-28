package com.example.serverconnect;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class DisplayMessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);
		// Show the Up button in the action bar.
		setupActionBar();
		ExecutorService pool = Executors.newFixedThreadPool(1);
		Future<String> future = pool.submit(new ServerConnect());
		// Create the text view
		String message;
		try {
			message = future.get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			message = "" + e;
		}
//		try {
//			message = ""+(new JSONObject(message)).getJSONArray("photo_paths").getString(0);
//		} catch (JSONException e) {
//			message = "nonono    " + e;
//		}
	    TextView textView = (TextView) findViewById(R.id.view_text);
	    textView.setText(message);
	    // Set the text view as the activity layout
	    //setContentView(textView);

	}
	
	public class ServerConnect implements Callable<String> {

		@Override
		public String call() throws Exception {
			JSONObject json = new JSONObject();
			try {
				json.accumulate("photo_id", 1);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String message = "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://www.doc.ic.ac.uk/project/2013/271/g1327111/db/view/view_links.php");
			try {
			StringEntity se = new StringEntity(json.toString());
			httpPost.setEntity(se);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			message = EntityUtils.toString(httpResponse.getEntity());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				message = "a  "+e;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				message = "b   "+e;
			} catch(Exception e) {message = "c   "+e;}
			return message;
		}
		
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
