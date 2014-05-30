package com.example.serverconnect;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

public class ServerConnection {
	private HttpClient httpClient;
	private ExecutorService threadPool;
	private final int MAX_ALLOWANCE = 1;
	private final int TIMEOUT = 30;
	
	public ServerConnection() {
		httpClient = new DefaultHttpClient();
		threadPool = Executors.newFixedThreadPool(MAX_ALLOWANCE);
	}
	
	public void closeConnection() {
		new Thread(new Runnable(){
			@Override
			public void run() {
				threadPool.shutdown();
				try {
					threadPool.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					threadPool.shutdownNow();
				}
				httpClient.getConnectionManager().shutdown();
			}}).start();
	}
	
	
	public JSONObject sendGETRequest(String URL, Map<String, String> params) throws InterruptedException, ExecutionException {
		return asyncSendGETRequest(URL, params).get();
	}
	
	public Future<JSONObject> asyncSendGETRequest(String URL, Map<String, String> params) throws InterruptedException, ExecutionException {
		Uri.Builder builder = new Uri.Builder();
		builder.encodedPath(URL);
		for(String key : params.keySet()) {
			builder.appendQueryParameter(key, params.get(key));
		}
		HttpGet request = new HttpGet(builder.toString());
		Future<JSONObject> future = threadPool.submit(new executeRequest(request));
		return future;
	}
	
	public JSONObject sendPOSTRequest(String URL, Map<String, String> params) throws InterruptedException, ExecutionException, UnsupportedEncodingException, JSONException {
		return asyncSendPOSTRequest(URL, params).get();
	}
	
	public Future<JSONObject> asyncSendPOSTRequest(String URL, Map<String, String> params) throws InterruptedException, ExecutionException, JSONException, UnsupportedEncodingException {
		HttpPost request = new HttpPost(URL);
		JSONObject json = new JSONObject();
		//MultipartEntityBuilder mpe = MultipartEntityBuilder.create();
		for(String key : params.keySet()) {
			json.accumulate(key, params.get(key));
		}
		request.setEntity(new StringEntity(json.toString()));
		Future<JSONObject> future = threadPool.submit(new executeRequest(request));
		return future;
	}
	
	
	private class executeRequest implements Callable<JSONObject> {
		
		HttpRequestBase request;
		
		public executeRequest(HttpRequestBase request) {
			this.request = request;
		}
		@Override
		public JSONObject call() throws Exception {
			HttpResponse httpResponse = httpClient.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			StringBuilder stringReply = new StringBuilder();
			String replyLine;
			while ((replyLine = reader.readLine()) != null) {
			    stringReply.append(replyLine);
			}
			return new JSONObject(stringReply.toString());
		}	
	}	
}
