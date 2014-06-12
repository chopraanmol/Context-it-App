package com.webapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PastContextsListView extends ListView {

	Context context;
	int x =0;
	public PastContextsListView(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public PastContextsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
		this.context = context;
        init();
	}
	
	private void init() {
		setOverScrollMode(OVER_SCROLL_ALWAYS);
		beginRequests();
	}
	
	@Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                    int scrollY, int scrollRangeX, int scrollRangeY,
                    int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            return super.overScrollBy(0, deltaY, 0, scrollY, 0, scrollRangeY, 0,
                            200, isTouchEvent);

    }
	/*
	@Override
	protected void onOverScrolled (int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
		x++;
		Log.d("Kaho", ""+x);
		ArrayAdapter<String> adapter = (ArrayAdapter<String>) getAdapter();
		
		//adapter.add("Kaho");
		//adapter.notifyDataSetChanged();
		// super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}

	*/
	
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
									JSONObject ret = serverConnection.sendGETRequest("http://www.doc.ic.ac.uk/project/2013/271/g1327111/db/view/view_photos.php", POSTMap);
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
		System.out.println(requestLinks(jsonToArrayList, 0));
		
	}

	private ArrayList<String> requestLinks(
			ArrayList<String> jsonToArrayList, int i) {
		//accepts a JSONObject of the agreed list format (alternating image urls/ids) and
		//the notional image "index" and returns the links associated with them
		Map<String,String> POSTMap = new HashMap<String,String>();								
		POSTMap.put("photo_id", jsonToArrayList.get(2*i+1));
		try {
			JSONObject ret = ServerConnection.connection.sendGETRequest("http://www.doc.ic.ac.uk/project/2013/271/g1327111/db/view/view_links.php", POSTMap);
			return (JSONToArrayList(ret));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private ArrayList<String> JSONToArrayList(JSONObject ret) throws JSONException {
		JSONArray url_set = ret.getJSONArray("web_urls");
		ArrayList<String> toRet = new ArrayList<String>();
		for(int i = 0; i < url_set.length(); i++) {
			toRet.add(url_set.getString(i));
		}
		return toRet;
	}
}
