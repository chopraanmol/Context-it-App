package com.webapp;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.support.v4.app.*;
import android.content.Intent;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;

public class MainActivity extends FragmentActivity {

	private LoginFragment login;
	private UiLifecycleHelper uiHelper;
	private Queue<SearchResult> results;
	private Session.StatusCallback callback = new Session.StatusCallback(){
	    @Override
	    public void call (Session session, SessionState state, Exception exception) {
	    	if (session != null) {
	    		onSessionStateChange(session, state, exception);
	    	}
	    }
	};
	
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
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            login = new LoginFragment();
    	    Session session = Session.getActiveSession();
    	    if (!(session != null &&
    	           session.isOpened())) {
                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, login).commit();
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
	    Session session = Session.getActiveSession();
	   /* if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }*/
	    uiHelper.onResume();    
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
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
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (state.isOpened() || state == SessionState.OPENING) {
			// Remove the LoginFragment, add the LandingFragment.
			if(state.isOpened()){
				Request.newMeRequest(session,
	                   new Request.GraphUserCallback() {
	         
						@Override
							public void onCompleted(GraphUser user, Response response) {
								if(user != null) {
									Log.d("Webapp", "User ID "+ user.getId());
									// send user id.
								} 
							}
				}).executeAsync();
			}
			
			transaction.replace(R.id.fragment_container, new LandingFragment());
			transaction.addToBackStack(null);
			transaction.commit();
		} else if (state.isClosed()) {
			// Remove whatever is there, add LoginFragment
			transaction.replace(R.id.fragment_container, new LoginFragment());
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
	
	public void jumpToTest(View v) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, new ResultContainerFragment());
		transaction.addToBackStack(null);
		transaction.commit();
	}
	

	public void displayFirstResults() {
		for(int i = 1; i <= numResultToShowLarge; i++) {
			if(resultCounter == 0) {
				break;
			}
			resultCounter--;
			renderResult(i, results.poll());
		}
	}

	public void replaceResult(int location) {
		// TODO Auto-generated method stub
		if (resultCounter == 0) {
			Log.d("ERROR", "replaceResult called even though there shouldn't be any "
							+ "more resultFragment");
		} else {
			resultCounter--;
			renderResult(location, results.poll());
		}	
	}

	private void renderResult(int location, SearchResult result) {
		// TODO Auto-generated method stub
		// check if there is any more result to show, if yes render it.
		// otherwise render empty fragment (or whatever needs to be done)
		// Create new fragment and transaction
		Fragment newFragment = new ResultFragment();
		Bundle args = new Bundle();
		args.putInt("locationId", location);
		args.putString("headLine", result.getHeadLine());
		args.putString("summary", result.getSummary());
		newFragment.setArguments(args);
		Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		FragmentTransaction transaction = frag.getChildFragmentManager().beginTransaction();
		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		switch(location) {
		case 1:
			transaction.replace(R.id.result_fragment1, newFragment);
			transaction.addToBackStack(null);
			break;
		case 2:
			transaction.replace(R.id.result_fragment2, newFragment);
			transaction.addToBackStack(null);
			break;
		case 3:
			transaction.replace(R.id.result_fragment3, newFragment);
			transaction.addToBackStack(null);
			break;
		default: //add unswipeable
			break;
		}

		// Commit the transaction
		transaction.commit();
	}

	public void sendFileToServer(File file) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, new WaitFragment());
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void receiveResults(Queue results) {
		// Incase we decided to receive results multiple times:
		this.results.addAll(results);
		resultCounter = results.size();
	}
	

}
