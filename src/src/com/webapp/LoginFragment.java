package com.webapp;


import java.util.Arrays;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.webapp.R;

public class LoginFragment extends Fragment{
	
	private static final String tag = "MainFragment";
	private MainActivity activity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	   
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.login_fragment, container, false);
	    
	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    /* Return result to fragment, not to main activity. */
	    //authButton.setFragment(this);
	    Typeface font = Typeface.createFromAsset(activity.getAssets(), "PrimeScript_PERSONAL_USE.ttf" );
		TextView title = (TextView) view.findViewById(R.id.app_title);
		title.setTypeface(font);
	    Typeface font2 = Typeface.createFromAsset(activity.getAssets(), "LABTSECW.ttf" );
		TextView des = (TextView) view.findViewById(R.id.app_description);
		des.setTypeface(font2);
		if(Session.getActiveSession().isOpened()) {
			Button goToLanding = (Button) view.findViewById(R.id.go_to_landing);
			font = Typeface.createFromAsset(activity.getAssets(), "fontawesome-webfont.ttf");
			goToLanding.setTypeface(font);
			goToLanding.setVisibility(View.VISIBLE);
			goToLanding.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					activity.goToLandingFragment();
				}
			});
		}
	    return view;
	}
	
	@Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	this.activity = (MainActivity) getActivity();
    }
	

	
}
