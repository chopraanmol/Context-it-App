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
	int x = 0;
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
	
	
}
