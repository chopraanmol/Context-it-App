package com.webapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.os.Build;

public class SelectFragment extends Fragment {
	
	Button cameraButton = null;
	
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
		View view = inflater.inflate(R.layout.select_fragment, container, false);
		cameraButton = (Button) view.findViewById(R.id.camera_button);
		cameraButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activateCamera();	
			}
		});
		return view;
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
		// TODO 
		// For now, it just makes the button disappear (for the sake of debugging)
		// Add code to activate camera, or transition to other fragment, etc.
		//cameraButton.setVisibility(View.INVISIBLE);
	}


}
