/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webapp;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StableArrayAdapter extends ArrayAdapter<String> {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    List<String> texts;
    View.OnTouchListener mTouchListener;
    Context context;
    LayoutInflater inflater;
    public StableArrayAdapter(Context context, int textViewResourceId,
            List<String> objects, View.OnTouchListener listener) {
        super(context, textViewResourceId, objects);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        texts = objects;
        Log.d("Debug", objects.isEmpty() ? "null" : "ok");
        mTouchListener = listener;
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        String item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View view = super.getView(position, convertView, parent);
    	View view = convertView;
    	String text = texts.get(position);
        view = inflater.inflate(R.layout.search_result_list_row, null);
        if (view != convertView) {
            // Add touch listener to every new view to track swipe motion
            view.setOnTouchListener(mTouchListener);
        }
        TextView t = (TextView) view.findViewById(R.id.title);
        t.setText(text);
        return view;
    }

}
