package com.webapp;

import java.io.File;
import java.io.IOException;

import com.facebook.widget.LoginButton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NoResultFragment extends Fragment {
	
	Context activity;
	File file = null;
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.no_result, container, false);
	    Typeface font = Typeface.createFromAsset( activity.getAssets(), "fontawesome-webfont.ttf" );
	    Button cameraButton = (Button) view.findViewById(R.id.camera);
	    cameraButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activateCamera();
				
			}
		});
	    cameraButton.setTypeface(font);
		
	    Button returnButton = (Button) view.findViewById(R.id.return_button);
	    returnButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MainActivity) activity).jumpToLandingFragment();
			}
		});
	    returnButton.setTypeface(font);
	    Typeface font2 = Typeface.createFromAsset(activity.getAssets(), "LABTSECW.ttf" );
	    TextView askAction = (TextView) view.findViewById(R.id.ask_action);
	    askAction.setTypeface(font2);
	    TextView sorry = (TextView) view.findViewById(R.id.sorry);
	    sorry.setTypeface(font2);
	    return view;
	}
	
	@Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	this.activity = getActivity();
    }
	

	public void activateCamera() {
		
		/*
		 * Capturing Camera Image will launch camera app request image capture
		 */
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri fileUri;
		try {
			file = (File.createTempFile("IMAGE_TEMP", null));
			fileUri = Uri.fromFile(file);
			Log.d("TAG", "BEFORE " + file.getAbsolutePath());
            if(!file.exists()) {
    			Log.d("TAG", "AFTER " + file.getAbsolutePath());
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            file.setWritable(true, false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// start the image capture Intent
		startActivityForResult(intent, 100);
	}

	/**
	 * Receiving activity result method will be called after closing the camera
	 * */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // if the result is capturing Image
	    if (requestCode == 100) {
	        if (resultCode == Activity.RESULT_OK) {
	            // successfully captured the image
	            // display it in image view
	            Toast.makeText(getActivity().getApplicationContext(),
	                    "Hooray!", Toast.LENGTH_SHORT)
	                    .show();
	           ((MainActivity) getActivity()).sendFileToServer(file);
	            
	        } else if (resultCode == Activity.RESULT_CANCELED) {
	            // user cancelled Image capture
	            Toast.makeText(getActivity().getApplicationContext(),
	                    "User cancelled image capture", Toast.LENGTH_SHORT)
	                    .show();
	        } else {
	            // failed to capture image
	            Toast.makeText(getActivity().getApplicationContext(),
	                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
	                    .show();
	        }
	    }
	}	
}
