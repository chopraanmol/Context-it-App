package com.webapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

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
	List<String> photos;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        				 ViewGroup container, 
	        				 Bundle savedInstanceState) {
	    beginRequests();
		View view = inflater.inflate(R.layout.past_contexts_fragment, container, false);
		
		Button b = (Button) view.findViewById(R.id.go_home);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).goToLandingFragment();
			}
		});
		b.setTypeface(Typeface.createFromAsset( getActivity().getAssets(), "fontawesome-webfont.ttf" ));
		return view;
	}
	
	@Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	this.context = getActivity();
    }
	
	public void getMorePhoto() {
		
	}
	
	private void beginRequests() {
		Request.newMeRequest(Session.getActiveSession(),
                new Request.GraphUserCallback() {
					@Override
						public void onCompleted(GraphUser user, Response response) {
							if(user != null) {
								ServerConnection serverConnection = ServerConnection.connection;
								Map<String,String> POSTMap = new HashMap<String,String>();								
								POSTMap.put("user_id", user.getId());
								try {
									JSONObject ret = serverConnection.sendGETRequest(
										"http://www.doc.ic.ac.uk/project/2013/271/g1327111/db/view/view_photos.php",
										POSTMap);
									if(ret != null) {
										requestAndDisplayPhotos(JSONToArrayList(ret));
									} else {
										Log.d("Conrad", "NULL!");
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							} 
						}

					private ArrayList<String> JSONToArrayList(JSONObject ret) throws JSONException {
						// TODO Auto-generated method stub
						JSONArray url_set = ret.getJSONArray("photo_paths");
						ArrayList<String> toRet = new ArrayList<String>();
						for(int i = 0; i < url_set.length(); i++) {
							toRet.add(url_set.getString(i));
						}
						return toRet;
					}
					
			}).executeAsync();
	}
	
	private void requestAndDisplayPhotos(
			ArrayList<String> jsonToArrayList) {
		//TODO: should be requesting image thumbnail and putting in temp file
		System.out.println(jsonToArrayList);
		//System.out.println(requestLinks(jsonToArrayList, 0));
		photos = jsonToArrayList;
		PastContextsListView listView = (PastContextsListView) getView().findViewById(R.id.past_context_lists);
		
		adapter = new PastContextAdapter(
                context, 
                R.id.context1,
                new ArrayList<String>(Arrays.asList(new String[photos.size()/2])),
                photos,
                new OnTouchListener() {
					
					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {
						return false;
					}
				});
		listView.setAdapter(adapter);
	}

	
	
	public static ArrayList<String> JSONToArrayList(JSONObject ret) throws JSONException {
		JSONArray url_set = ret.getJSONArray("web_urls");
		ArrayList<String> toRet = new ArrayList<String>();
		for(int i = 0; i < url_set.length(); i++) {
			toRet.add(url_set.getString(i));
		}
		return toRet;
	}
}
