package com.webapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WaitFragment extends Fragment {
	
	MainActivity main;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		main = (MainActivity) getActivity();
	};

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
		Log.d("FUKC","AAAAAAAAAAAAAAAAAAAAAAAAA");
		View view = inflater.inflate(R.layout.wait_fragment, container, false);
		return view;
	}

	
}
