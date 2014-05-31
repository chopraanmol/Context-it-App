package com.example.serverconnect;

import java.io.BufferedReader;
import java.io.InputStream;
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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
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
	
	/*It is safe to shutdown the connection even while processes are communicating with the server.
	But there is a timeout of 30 seconds for all the processes to finish their work before they are killed.*/
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
	
	//Calling this will block the calling thread.
	public JSONObject sendGETRequest(String URL, Map<String, String> params) throws InterruptedException, ExecutionException {
		return asyncSendGETRequest(URL, params).get();
	}
	
	//Calling this will not block the calling thread.
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
	
	//Calling this will block the calling thread. Fill in the not needed parameters with null.
	public JSONObject sendPOSTRequest(String URL, Map<String, String> params, Map<String, InputStream> files) throws InterruptedException, ExecutionException, UnsupportedEncodingException, JSONException {
		return asyncSendPOSTRequest(URL, params, files).get();
	}
	
	//Calling this will not block the calling thread. Fill in the not needed parameters with null.
	public Future<JSONObject> asyncSendPOSTRequest(String URL, Map<String, String> params, Map<String, InputStream> files) throws InterruptedException, ExecutionException, JSONException, UnsupportedEncodingException {
		HttpPost request = new HttpPost(URL);
		MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
		if(params!=null) {
			for(String key : params.keySet()) {
				multipartEntity.addTextBody(key, params.get(key));
			}
		}
		if(files!=null) {
			for(String fileName : files.keySet()) {
				multipartEntity.addPart(fileName, new InputStreamBody(files.get(fileName),ContentType.MULTIPART_FORM_DATA, fileName));
			}
		}
		request.setEntity(multipartEntity.build());
		Future<JSONObject> future = threadPool.submit(new executeRequest(request));
		return future;
	}
	
	//Thread to communicate with server.
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
