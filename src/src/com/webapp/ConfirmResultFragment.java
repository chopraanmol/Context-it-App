package com.webapp;

import java.util.List;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ConfirmResultFragment extends Fragment{

	List<String> results;
	Context context;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = getActivity();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		results = savedInstanceState.getStringArrayList("results_to_confirm");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.confirm_result_fragment, container);
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(
                context, 
                R.layout.search_result_list_row,
                results);
    	ListView mListView = (ListView) view.findViewById(R.id.list_to_confirm);
        SwingLeftInAnimationAdapter newAdapter = new SwingLeftInAnimationAdapter(mAdapter);
        newAdapter.setAbsListView(mListView);
        mListView.setAdapter(newAdapter);
    	return view;
    }
	    
}
