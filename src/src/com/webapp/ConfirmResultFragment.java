package com.webapp;

import java.util.List;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
		results = getArguments().getStringArrayList("results_to_confirm");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.confirm_result_fragment, container, false);
		Button shareButton = (Button) view.findViewById(R.id.share_button);
		shareButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				share();
			}
		});
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(
                context, 
                R.layout.search_result_list_row,
                R.id.title,
                results);
    	ListView mListView = (ListView) view.findViewById(R.id.list_to_confirm);
        mListView.setAdapter(mAdapter);
    	return view;
    }
	
	private void share() {
		//TODO 
	}
	    
}
