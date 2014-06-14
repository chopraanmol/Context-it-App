package com.webapp;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PastContextSelectedFragment extends DialogFragment {
	
	ArrayList<String> links;
	String photoUrl;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		Bundle b = getArguments();
		links = b.getStringArrayList("links");
		photoUrl = b.getString(photoUrl);
		super.onCreate(savedInstance);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        				 ViewGroup container, 
	        				 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.past_context_selected, container, false);
		ArrayAdapter<String> mAdapter = new StableArrayAdapter(
                getActivity(), 
                R.id.title,
                links,
                new OnTouchListener() {
					
					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {
						return false;
					}
				});
    	ListView mListView = (ListView) view.findViewById(R.id.links);
        mListView.setAdapter(mAdapter);
		return view;
	}
	

}
