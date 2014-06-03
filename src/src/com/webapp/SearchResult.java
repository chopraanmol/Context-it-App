package com.webapp;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchResult {
	private String headLine;
	private String summary;
	
	// Dummy constructor
	public SearchResult(JSONObject jsonObject) {
		try {
			headLine = jsonObject.getString("url");
			summary = jsonObject.getString("desc");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Implement proper constructor that takes Jason object
	
	public String getHeadLine() {
		return headLine;
	}
	
	public String getSummary() {
		return summary;
	}
	
	@Override
	public String toString() {
		return headLine + "\n\n" + summary;
		
	}
}
