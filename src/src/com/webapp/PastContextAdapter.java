package com.webapp;


import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PastContextAdapter extends ArrayAdapter<String> {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    List<String> links;
    List<String> photos;
    View.OnTouchListener mTouchListener;
    Context context;
    LayoutInflater inflater;
    
    
    public PastContextAdapter(Context context, int textViewResourceId,
            List<String> links, List<String> photos, View.OnTouchListener listener) {
        super(context, textViewResourceId, links);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.links = links;
        this.photos = photos;
        mTouchListener = listener;
       
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
     }

    @Override
    public long getItemId(int position) {
        String item = getItem(position);
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View view = super.getView(position, convertView, parent);
    	View view = convertView;
    	String text2 = "WAEFAWDSFDFFDFF";
        view = inflater.inflate(R.layout.past_contexts_row, null);
        if (view != convertView) {
            // Add touch listener to every new view to track swipe motion
            view.setOnTouchListener(mTouchListener);
        }
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "OleoScript-Bold.ttf" );
        TextView t = (TextView) view.findViewById(R.id.context1);
        t.setText(getItem(position));
        t.setTypeface(tf);
        t = (TextView) view.findViewById(R.id.context2);
        t.setText(text2);
        t.setTypeface(tf);
        String url = toThumbnailURL(photos.get(position*2));
        // TODO: here, add the logic of retrieving the photo from cache/server 
        Cache cache = VolleyCacheManager.getInstance().getRequestQueue().getCache();
        Entry entry = cache.get(url);
        Bitmap image;
        final ImageView imageView = (ImageView) view.findViewById(R.id.list_image
        		);
        if(entry != null){
            image = BitmapFactory.decodeByteArray(entry.data, 0, entry.data.length);
        } else{
            // Cached response doesn't exists. Make network call here
        	ImageLoader imageLoader = VolleyCacheManager.getInstance().getImageLoader();
        	 
        	// If you are using normal ImageView
        	imageLoader.get(url, new ImageListener() {
        	 
        	    @Override
        	    public void onErrorResponse(VolleyError error) {
        	        Log.d("Webapp", "Image Load Error: " + error.getMessage());
        	    }
        	 
        	    @Override
        	    public void onResponse(ImageContainer response, boolean arg1) {
        	        if (response.getBitmap() != null) {
        	            // load image into imageview        	
        	            imageView.setImageBitmap(response.getBitmap());
        	        }
        	    }
        	});
        }
        return view;
    }

	private String toThumbnailURL(String string) {
		String cut = string.substring(0, string.lastIndexOf(".jpg"));
		return cut + "_thumbnail.jpg";
	}
    


}
