package com.webapp;

import java.io.File;
import java.io.IOException;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;
import android.provider.MediaStore;

public class LandingFragment extends Fragment {
	
	Button cameraButton = null;
	File file = null;
	Activity activity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// final Button cameraButton = (Button) findViewById(R.id.cameraButton);
        
		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.landing_fragment, container, false);
		Typeface font = Typeface.createFromAsset( activity.getAssets(), "fontawesome-webfont.ttf" );
		cameraButton = (Button) view.findViewById(R.id.camera_button);
		cameraButton.setTypeface(font);
		cameraButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activateCamera();	
			}
		});
		return view;
	}
	
	@Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	this.activity = getActivity();
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
