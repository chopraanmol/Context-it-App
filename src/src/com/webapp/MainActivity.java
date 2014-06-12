package com.webapp;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import android.support.v4.app.*;
import android.content.Intent;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class MainActivity extends FragmentActivity {

	public boolean preventLanding = false;
	private LoginFragment login;
	public UiLifecycleHelper uiHelper;
	private Queue<SearchResult> results;
	private Session.StatusCallback callback = new Session.StatusCallback(){
	    @Override
	    public void call (Session session, SessionState state, Exception exception) {
	    	if (session != null) {
	    		onSessionStateChange(session, state, exception);
	    	}
	    }
	};
	
	File file = null;
	
	private int resultCounter = 0;
	private int resultToShowCounter = 0;
	
	
	boolean large = false;
	private final int numResultToShowLarge = 3;
	
	
	/* loads the layout in the main activity.
	   If there is no saved state -> create a new fragment, load it.
	   If there is 				  -> restore that. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		results = new LinkedList<SearchResult>();
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		//TODO UPDATE LARGE FIELD HERE.
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
    			System.out.println("SAVED INSTANCE NON-NULL");

                return;
            }

            // Create a new Fragment to be placed in the activity layout
    	    if(Session.openActiveSession(this, true, callback) == null) {
            	login = new LoginFragment();
    	    	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, login).commitAllowingStateLoss();
    	    }
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();    
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	private void onSessionStateChange(Session session, SessionState state,
			  Exception exception) {
		if (state.isOpened() && state != SessionState.OPENING) {
			// Remove the LoginFragment, add the LandingFragment.
			if(state.isOpened()){
				Request.newMeRequest(session,
	                   new Request.GraphUserCallback() {
	         
						@Override
							public void onCompleted(GraphUser user, Response response) {
							Log.d("Conrad", "FUCK YOU CONRAD");
								if(user != null) {
									ServerConnection IDConnection = ServerConnection.connection;
									Map<String, String> POSTMap = new HashMap<String, String>();
									POSTMap.put("user_id", user.getId());
									try {
										JSONObject ret = IDConnection.sendPOSTRequest("http://www.doc.ic.ac.uk/project/2013/271/g1327111/db/create/create_user.php", POSTMap, null);
										switch(ret.getInt("status")) {
										case 1:
								            Toast.makeText(getApplicationContext(),
								                    "New user entered to database", Toast.LENGTH_SHORT)
								                    .show();
								            break;
										case 3:
								            Toast.makeText(getApplicationContext(),
								                    "Existing user acknowledged", Toast.LENGTH_SHORT)
								                    .show();
								            break;
								        default:
								            Toast.makeText(getApplicationContext(),
								                    "YOU MESSED UP", Toast.LENGTH_SHORT)
								                    .show();
								            break;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									// send user id.
								}
								if(!preventLanding) {
									goToLandingFragment();
								}
							}
				}).executeAsync();
			}
			
		} else  {
			System.out.println("HELLO HERE");
			if (state.isClosed()) {
			// Remove whatever is there, add LoginFragment
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, new LoginFragment());
			transaction.addToBackStack(null);
			transaction.commitAllowingStateLoss();
			}
		}
	}
	
	public void goToLandingFragment() {
		preventLanding = true;
		Log.d("Conrad", "dunno why");
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, new LandingFragment());
		transaction.addToBackStack(null);
		transaction.commitAllowingStateLoss();
	}
	
	public void jumpToTest(View v) {
		
		/*ArrayList<String> l = new ArrayList<String>(Arrays.asList("blah", "blah", "blah"));
		Fragment newFragment = new ConfirmResultFragment();
		Bundle args = new Bundle();
		args.putStringArrayList("results_to_confirm",l);
		newFragment.setArguments(args);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.commitAllowingStateLoss();*/
		
		Fragment newFragment = new PastContextsFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.commitAllowingStateLoss();

	}

	public void sendFileToServer(File file) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		Fragment newFragment = new WaitFragment();
		Bundle args = new Bundle();
		args.putSerializable("file", file);
		newFragment.setArguments(args);
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commitAllowingStateLoss();
	}
	
	public void receiveResults(Queue results) {
		// Incase we decided to receive results multiple times:
		this.results.addAll(results);
		resultCounter = results.size();
	}
	
	public void testSwipeView(View v) {
		goToSwipeView(							 
				new ArrayList<String>()/*Arrays.asList(
				/* "headline 1 : blah blah",
				 "headline 2 : asdfghj",
				 "headline 3 : wertyrew",
				 "headline 4 : weardfgvx",
				 "headline 5 : weaggggg",
				 "headline 6 : wawearwe",
				 "headline 7 : twatwea",
				 "headline 8 : twaeaa",
				 "headline 9 : waeaea",
				 "headline 10: wawaaa"))*/, null, v);
	}
	
	public void goToSwipeView(ArrayList<String> arrayList, File f, View v) {
		if (arrayList.isEmpty()) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			/* TODO */
	        transaction.replace(R.id.fragment_container, new NoResultFragment());
	        // transaction.addToBackStack(null);
	        transaction.commitAllowingStateLoss();
	        return;
		}
		arrayList.add("");
		SwipeResult s = new SwipeResult();
		Bundle b = new Bundle();
		b.putStringArrayList("search_results", arrayList);
		b.putSerializable("file", f);
		s.setArguments(b);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, s);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
	}
	
	public void goToListOfSavedContext(File file, List<String> results) {
		ConfirmResultFragment f = new ConfirmResultFragment();
		Bundle b = new Bundle();
		b.putStringArrayList("results_to_confirm", (ArrayList<String>) results);
		b.putSerializable("file", file);
		f.setArguments(b);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, f);
        transaction.addToBackStack(null);
        transaction.commit();
	}
	
public void activateCamera() {
		
		/*
		 * Capturing Camera Image will launch camera app request image capture
		 */
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri fileUri;
		try {
			file = (File.createTempFile("IMAGE_TEMP", null));
			fileUri = Uri.fromFile(file);
			Log.d("TAG", "BEFORE " + file.getAbsolutePath());
            if(!file.exists()) {
    			Log.d("TAG", "AFTER " + file.getAbsolutePath());
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            file.setWritable(true, false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// start the image capture Intent
		startActivityForResult(intent, 100);
	}


}
