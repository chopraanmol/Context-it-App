package com.webapp;

public class SearchResult {
	private String headLine;
	private String summary;
	
	// Dummy constructor
	public SearchResult() {
		headLine = "headline";
		summary = "summary";
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
