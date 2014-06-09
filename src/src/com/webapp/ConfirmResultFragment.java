package com.webapp;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.facebook.Session;
import com.facebook.widget.FacebookDialog;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
	    ((MainActivity) getActivity()).uiHelper.onActivityResult(requestCode, resultCode, data);
	    ((MainActivity) getActivity()).goToLandingFragment();
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
		Session session = Session.getActiveSession();
        Session.NewPermissionsRequest newPermissionsRequest = new Session
          .NewPermissionsRequest(this, Arrays.asList("publish_actions"));
        session.requestNewPublishPermissions(newPermissionsRequest);
    	System.out.println(session.toString());
    	FacebookDialog shareDialog = new FacebookDialog.PhotoShareDialogBuilder(getActivity())
    	.addPhotoFiles(
    			java.util.Collections.singleton((File)(getArguments().getSerializable("file"))))
        .build();
    	((MainActivity) getActivity()).uiHelper.trackPendingDialogCall(shareDialog.present());
	}
	    
}
