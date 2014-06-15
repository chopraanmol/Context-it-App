package com.webapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.OpenGraphActionDialogBuilder;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
	    //((MainActivity) getActivity()).goToLandingFragment();
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
		Typeface font = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf" );
		shareButton.setTypeface(font);
		final MutableString s = new MutableString();
        final GestureDetectorCompat mDetector = 
        		new GestureDetectorCompat(getActivity(), new GestureDetector.SimpleOnGestureListener(){
        			@Override
        			public boolean onDown(MotionEvent event) {
						return true;
        				
        			}
        			
    			   @Override
    			   public boolean onSingleTapUp(MotionEvent event) {
    				   openWebBrowser(SwipeResult.splitText(s.string)[0]);
		        		return true;
    			   }

        		});
		ArrayAdapter<String> mAdapter = new StableArrayAdapter(
                context, 
                R.id.title,
                results,
                new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
			        	s.string = ((TextView)((RelativeLayout) v).findViewById(R.id.title)).getText().toString();
			        	return(mDetector.onTouchEvent(event));
					}
                	
				});
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
    	String title = "";
    	for (String s : results) {
    		title += (s + "\n");
    	}
    	Bundle params = new Bundle();
    	Arrays.asList(new File[]{(File)(getArguments().getSerializable("file"))});
    	params.putParcelable("source", BitmapFactory.decodeFile(((File)(getArguments().getSerializable("file"))).getAbsolutePath()));

    	params.putString("message", title);
    	/* make the API call */
    	new Request(
    	    session,
    	    "/me/photos",
    	    params,
    	    HttpMethod.POST,
    	    new Request.Callback() {
    	        public void onCompleted(Response response) {
    	        }
    	    }
    	).executeAsync();
    	((MainActivity) getActivity()).goToLandingFragment();
	}
	
    public void openWebBrowser(String url) {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    	context.startActivity(browserIntent);
    }
	
}

class MutableString {
	public String string = "";
}