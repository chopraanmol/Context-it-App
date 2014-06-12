package com.webapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PastContextsFragment extends Fragment{
	
	Context context;
	List<String> results = new ArrayList<String>(Arrays.asList("1", "2", "3"));
	ArrayAdapter<String> adapter;
	List<String> photo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        				 ViewGroup container, 
	        				 Bundle savedInstanceState) {
		Log.d("KAHO", "CREATING PAST");
		View view = inflater.inflate(R.layout.past_contexts_list, container, false);
		PastContextsListView listView = (PastContextsListView) view.findViewById(R.id.past_context_lists);
		
		adapter = new PastContextAdapter(
                context, 
                R.id.context1,
                results,
                new OnTouchListener() {
					
					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {
						return false;
					}
				});
		listView.setAdapter(adapter);
		
		Button b = (Button) view.findViewById(R.id.get_more);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getMorePhoto();
			}
		});
		return view;
	}
	
	@Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	this.context = getActivity();
    }
	
	public void getMorePhoto() {
		
	}
	
}
