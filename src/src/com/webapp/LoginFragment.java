package com.webapp;


import java.util.Arrays;

import android.support.v4.app.Fragment;
import android.content.Intent;
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

public class LoginFragment extends Fragment{
	
	private static final String tag = "MainFragment";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	   
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.login_fragment, container, false);
	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    /* Return result to fragment, not to main activity. */
	    //authButton.setFragment(this);
	    return view;
	}
	
}
