package com.webapp;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
