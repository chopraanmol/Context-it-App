package com.webapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class PastContextsFragment extends Fragment{
	
	Context activity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        				 ViewGroup container, 
	        				 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.past_contexts_list, container, false);
		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(activity));

		gridview.setOnItemClickListener(new OnItemClickListener() {
		        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		        	//TODO
		           return;
		        }
		    });
		return view;
	}
	
	@Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	this.activity = getActivity();
    }
}
