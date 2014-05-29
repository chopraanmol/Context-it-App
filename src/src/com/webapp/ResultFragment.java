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

public class ResultFragment extends Fragment {
	
	private int locationId;
	
	MainActivity main;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		main = (MainActivity) getActivity();
	};
	
	public void setArguments(Bundle args) {
		locationId = args.getInt("locationId");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.result_fragment, container, false);
		Button saveButton = (Button) view.findViewById(R.id.save_result_button);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveResult();	
			}
		});
		
		Button discardButton = (Button) view.findViewById(R.id.discard_result_button);
		discardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				discardResult();	
			}
		});
		
		Button seeOnlineButton = (Button) view.findViewById(R.id.see_online_button);
		seeOnlineButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showResultOnline();	
			}
		});
		return view;
	}
	
	private void saveResult() {
		// TODO Auto-generated method stub	
		Log.d("Webapp", "save button is pressed");
		main.replaceResult(locationId);
	}
	
	private void showResultOnline() {
		// TODO Auto-generated method stub	
		Log.d("Webapp", "show result button is pressed");
	}

	private void discardResult() {
		// TODO Auto-generated method stub
		Log.d("Webapp", "discard button is pressed");
		main.replaceResult(locationId);
	}

	
}
