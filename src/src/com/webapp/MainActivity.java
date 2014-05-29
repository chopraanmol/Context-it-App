package com.webapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }
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
		transaction.replace(R.id.fragment_container, new ResultFragment(1));
		transaction.addToBackStack(null);
		transaction.commit();
	}
	

	private void displayFirstResults() {
		// This should be called by some callback object probably.
		// inflate new view, which is result_container_fragment.xml (haven't implemented that yet)  
	}

	public void replaceResult(int location) {
		// TODO Auto-generated method stub
		if (resultCounter == 0) {
			Log.d("ERROR", "replaceResult called even though there shouldn't be any "
							+ "more resultFragment");
		} else {
			resultCounter--;
			renderResult(location);
		}
		
	}

	private void renderResult(int location) {
		// TODO Auto-generated method stub
		// check if there is any more result to show, if yes render it.
		// otherewise render empty fragment (or whatever needs to be done)
	}
	

}
