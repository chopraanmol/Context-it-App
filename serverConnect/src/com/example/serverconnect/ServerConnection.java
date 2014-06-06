package com.example.serverconnect;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Pair;

public class ServerConnection {
	private HttpClient httpClient;
	private ExecutorService threadPool;
	private final int MAX_ALLOWANCE = 1;
	private final int TIMEOUT = 30;
	private boolean connection_closed;

	public ServerConnection() {
		httpClient = new DefaultHttpClient();
		threadPool = Executors.newFixedThreadPool(MAX_ALLOWANCE);
		connection_closed = false;
	}

	/*
	 * It is safe to shutdown the connection even while processes are
	 * communicating with the server. But there is a timeout of 30 seconds for
	 * all the processes to finish their work before they are killed.
	 */
	public void closeConnection() {
		if (connection_closed) {
			return;
		}
		connection_closed = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				threadPool.shutdown();
				try {
					threadPool.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					threadPool.shutdownNow();
				}
				httpClient.getConnectionManager().shutdown();
			}
		}).start();
	}

	// Calling this will block the calling thread.
	public JSONObject sendGETRequest(String URL, Map<String, String> params)
			throws InterruptedException, ExecutionException {
		return asyncSendGETRequest(URL, params).get();
	}

	// Calling this will not block the calling thread.
	public Future<JSONObject> asyncSendGETRequest(String URL,
			Map<String, String> params) throws InterruptedException,
			ExecutionException {
		Uri.Builder builder = new Uri.Builder();
		builder.encodedPath(URL);
		for (String key : params.keySet()) {
			builder.appendQueryParameter(key, params.get(key));
		}
		HttpGet request = new HttpGet(builder.toString());
		Future<JSONObject> future = threadPool.submit(new executeRequest(
				request));
		return future;
	}

	/*
	 * Calling this will block the calling thread. Fill in the not needed
	 * parameters with null. Map<key to get file details,
	 * Pair<filename,InputStream>>
	 */
	public JSONObject sendPOSTRequest(String URL, Map<String, String> params,
			Map<String, Pair<String, InputStream>> files)
			throws InterruptedException, ExecutionException, JSONException,
			IOException {
		return asyncSendPOSTRequest(URL, params, files).get();
	}

	/*
	 * Calling this will not block the calling thread. Fill in the not needed
	 * parameters with null. Map<key to get file details,
	 * Pair<filename,InputStream>>
	 */
	public Future<JSONObject> asyncSendPOSTRequest(String URL,
			Map<String, String> params,
			Map<String, Pair<String, InputStream>> files)
			throws InterruptedException, ExecutionException, JSONException,
			IOException {
		HttpPost request = new HttpPost(URL);
		MultipartEntityBuilder multipartEntity = MultipartEntityBuilder
				.create();
		if (params != null) {
			for (String key : params.keySet()) {
				multipartEntity.addTextBody(key, params.get(key),
						ContentType.TEXT_PLAIN);
			}
		}
		if (files != null) {
			for (String key : files.keySet()) {
				// ENABLE THE FIRST VERSION IF CHUNKING IS AVALIABLE ON THE
				// SERVER AND UNCOMMENT ITS IMPORT ON TOP.
				 /*multipartEntity.addPart(key, new
				 InputStreamBody(files.get(key).second,
				 ContentType.APPLICATION_OCTET_STREAM,files.get(key).first));*/
				multipartEntity.addBinaryBody(key,
						IOUtils.toByteArray(files.get(key).second),
						ContentType.APPLICATION_OCTET_STREAM,
						files.get(key).first);
			}
		}
		request.setEntity(multipartEntity.build());
		Future<JSONObject> future = threadPool.submit(new executeRequest(
				request));
		return future;
	}

	// Thread to communicate with server and give back a Json object.
	private class executeRequest implements Callable<JSONObject> {

		HttpRequestBase request;

		public executeRequest(HttpRequestBase request) {
			this.request = request;
		}

		@Override
		public JSONObject call() throws Exception {
			HttpResponse httpResponse = httpClient.execute(request);
			InputStream in = httpResponse.getEntity().getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder stringReply = new StringBuilder();
			String replyLine;
			while ((replyLine = reader.readLine()) != null) {
				stringReply.append(replyLine);
			}
			in.close();
			return new JSONObject(stringReply.toString());
		}
	}

	// Blocking version to obtain a file at the specified url.
	public InputStream getFile(String url, File file) throws InterruptedException,
			ExecutionException {
		return asyncGetFile(url, file).get();
	}

	// non-blocking version to obtain a url at the specified url.
	public Future<InputStream> asyncGetFile(String url, File file) {
		HttpGet request = new HttpGet(url);
		Future<InputStream> future = threadPool.submit(new getFileRequest(request));
		return future;
	}

	// Thread to communicate with server and give back a file object.
	private class getFileRequest implements Callable<InputStream> {

		HttpRequestBase request;

		public getFileRequest(HttpRequestBase request) {
			this.request = request;
		}

		@Override
		public InputStream call() throws Exception {
			HttpResponse httpResponse = httpClient.execute(request);
			InputStream in = httpResponse.getEntity().getContent();
			return in;
			/*OutputStream out = new FileOutputStream(file);
			IOUtils.copy(in, out);
			in.close();
			out.close();*/
		}
	}
}