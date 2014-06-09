package com.example.serverconnect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Pair;

public class ServerConnection {
	private ExecutorService threadPool;

	public ServerConnection(int numberOfActiveConnections) {
		threadPool = Executors.newFixedThreadPool(numberOfActiveConnections);
	}

	// Calling this will block the calling thread.
	public JSONObject sendGETRequest(String URL, Map<String, String> params)
			throws InterruptedException, ExecutionException, IOException {
		return asyncSendGETRequest(URL, params).get();
	}

	// Calling this will not block the calling thread.
	public Future<JSONObject> asyncSendGETRequest(String URL,
			Map<String, String> params) throws IOException {
		Uri.Builder builder = new Uri.Builder();
		builder.encodedPath(URL);
		for (String key : params.keySet()) {
			builder.appendQueryParameter(key, params.get(key));
		}
		URL url = new URL(builder.toString());
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		urlConnection.setRequestMethod("GET");
		Future<JSONObject> future = threadPool.submit(new executeRequest(null,
				urlConnection));
		return future;
	}

	/*
	 * Calling this will block the calling thread. Fill in the not needed
	 * parameters with null. Map<key to get file details,
	 * Pair<filename,InputStream>>
	 */
	public JSONObject sendPOSTRequest(String URL, Map<String, String> params,
			Map<String, Pair<String, InputStream>> files)
			throws InterruptedException, ExecutionException, IOException {
		return asyncSendPOSTRequest(URL, params, files).get();
	}

	/*
	 * Calling this will not block the calling thread. Fill in the not needed
	 * parameters with null. Map<key to get file details,
	 * Pair<filename,InputStream>>
	 */
	public Future<JSONObject> asyncSendPOSTRequest(String URL,
			Map<String, String> params,
			Map<String, Pair<String, InputStream>> files) throws IOException {
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
				multipartEntity.addPart(key, new InputStreamBody(
						files.get(key).second,
						ContentType.APPLICATION_OCTET_STREAM,
						files.get(key).first));
			}
		}
		HttpEntity request = multipartEntity.build();
		URL url = new URL(URL);
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);
		urlConnection.setRequestProperty(request.getContentType().getName(),
				request.getContentType().getValue());
		urlConnection.setChunkedStreamingMode(0);
		Future<JSONObject> future = threadPool.submit(new executeRequest(
				request, urlConnection));
		return future;
	}

	// Thread to communicate with server and give back a Json object.
	private class executeRequest implements Callable<JSONObject> {

		HttpEntity request;
		HttpURLConnection urlConnection;

		public executeRequest(HttpEntity request,
				HttpURLConnection urlConnection) {
			this.request = request;
			this.urlConnection = urlConnection;
		}

		@Override
		public JSONObject call() throws JSONException {
			StringBuilder stringReply = new StringBuilder();
			try {
				if (request != null) {
					request.writeTo(urlConnection.getOutputStream());
				}
				InputStream in = urlConnection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				String replyLine;
				while ((replyLine = reader.readLine()) != null) {
					stringReply.append(replyLine);
				}
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				stringReply.setLength(0);
				stringReply.append((new JSONObject())
						.accumulate("status", "-1").toString());

			}
			finally {
				urlConnection.disconnect();
			}
			return new JSONObject(stringReply.toString());
		}
	}

	// Blocking version to obtain a file at the specified url.
	public Boolean getFile(String url, PreciousFile file) throws InterruptedException, ExecutionException, IOException {
		return asyncGetFile(url, file).get();
	}

	// non-blocking version to obtain a file at the specified url.
	public Future<Boolean> asyncGetFile(String URL, PreciousFile file) throws IOException {
		URL url = new URL(URL);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("GET");
		Future<Boolean> future = threadPool.submit(new getFileRequest(urlConnection,
				file));
		return future;
	}

	// Thread to communicate with server and give back an inputstream object of
	// a file.
	private class getFileRequest implements Callable<Boolean> {

		HttpURLConnection urlConnection;
		PreciousFile file;

		public getFileRequest(HttpURLConnection urlConnection, PreciousFile file) {
			this.urlConnection =urlConnection;
			this.file = file;
		}

		@Override
		public Boolean call() {
			boolean success = false;
			try {
				InputStream in = urlConnection.getInputStream();
				success = file.write(in);
			} catch (Exception e) {
				success = false;
				file.clearFile();
			}
			finally {
				urlConnection.disconnect();
			}
			return success;
		}
	}
}