package com.webapp;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class WaitFragment extends Fragment {
	
	MainActivity main;
	
	File file;
	
	public void setArguments(Bundle args) {
		file = (File) args.getSerializable("file");
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		main = (MainActivity) getActivity();
	};

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.wait_fragment, container, false);
		Request.newMeRequest(Session.getActiveSession(),
                new Request.GraphUserCallback() {
					@Override
						public void onCompleted(GraphUser user, Response response) {
							if(user != null) {
								ServerConnection ImageConnection = new ServerConnection();
								Map<String,String> POSTMap = new HashMap<String,String>();
								Map<String, Pair<String, InputStream>> imageMap = new HashMap<String,Pair<String,InputStream>>();
								POSTMap.put("user_id", user.getId());
								try {
									imageMap.put("file", new Pair<String, InputStream>("input.jpg", new FileInputStream(file)));
									Log.d("Conrad","PENIIIIIIIIIIIIIIIIIIIIIS");
									JSONObject ret = ImageConnection.sendPOSTRequest("http://www.doc.ic.ac.uk/project/2013/271/g1327111/process/process_image.php", POSTMap, imageMap);
									if(ret != null) {
										Log.d("Conrad", ret.toString());
										main.goToSwipeView(JSONToArrayList(ret));
									} else {
							            Toast.makeText(main.getApplicationContext(),
							                    "New user entered to database", Toast.LENGTH_SHORT)
							                    .show();
									}
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ExecutionException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								ImageConnection.closeConnection();
							} 
						}

					private ArrayList<String> JSONToArrayList(JSONObject ret) {
						// TODO Auto-generated method stub
						return new ArrayList<String>(Arrays.asList("headline 1 : blah blah",
								 "headline 2: asdfghj",
								 "headline 3: wertyrew",
								 "headline 4:weardfgvx",
								 "headline 5: weaggggg",""));
					}
			}).executeAsync();
		return view;
	}

	
}
