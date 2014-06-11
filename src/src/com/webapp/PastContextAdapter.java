package com.webapp;


import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PastContextAdapter extends ArrayAdapter<String> {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    List<String> texts;
    View.OnTouchListener mTouchListener;
    Context context;
    LayoutInflater inflater;
    int height_of_element;
    final int num_of_rows = 3;
    
    
    public PastContextAdapter(Context context, int textViewResourceId,
            List<String> objects, View.OnTouchListener listener) {
        super(context, textViewResourceId, objects);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        texts = objects;
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
    	String text1 = "HELLO!";
    	String text2 = "WAEFAWDSFDFFDFF";
        view = inflater.inflate(R.layout.past_contexts_row, null);
        if (view != convertView) {
            // Add touch listener to every new view to track swipe motion
            view.setOnTouchListener(mTouchListener);
        }
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "OleoScript-Bold.ttf" );
        TextView t = (TextView) view.findViewById(R.id.context1);
        t.setText(text1);
        t = (TextView) view.findViewById(R.id.context2);
        t.setText(text2);
        t.setTypeface(tf);
              
        return view;
    }

}
