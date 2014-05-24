package com.webapp;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.webapp.R;

public class MainFragment extends Fragment{
	
	private static final String tag = "MainFragment";
	
	/* Listens to the change of the session status. */
	private Session.StatusCallback callback = new Session.StatusCallback(){
	    @Override
	    public void call (Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	/* Future use - change in UI on log out, log in etc */
	private UiLifecycleHelper uiHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.main, container, false);
	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    /* Return result to fragment, not to main activity. */
	    authButton.setFragment(this);
	    return view;
	}
	
	/* Called from call function of StatusCallBack listener.(this.callback) */
	private void onSessionStateChange(Session session, SessionState state,
									  Exception exception) {
	    if (state.isOpened()) {
	        Log.i(tag, "Logged in...");
	    } else if (state.isClosed()) {
	        Log.i(tag, "Logged out...");
	    }
	}
}
