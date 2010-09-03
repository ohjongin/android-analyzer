package org.androidanalyzer.transport.impl.json;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.zip.GZIPOutputStream;

import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.utils.Logger;
import org.androidanalyzer.transport.Reporter;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

/**
 * HTTPJSONReporter is used to sending final report to the backend over
 * HTTP protocol.
 * 
 */

public class HTTPJSONReporter extends Reporter {

	private static final String X_ANDROID_ANALYZER_REPORT_MD5 = "X_ANDROID_ANALYZER_REPORT_MD5";

	private static final String TAG = "Analyzer-HTTPJSONReporter";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.androidanalyzer.transport.Reporter#send(org.androidanalyzer.core.Data,
	 * java.net.URL)
	 */
	public String send(Data data, URL host) throws Exception {
		int timeoutSocket = 5000;
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpPost httpost = new HttpPost(Reporter.getHost());
		JSONObject jObject = null;
		jObject = JSONFormatter.format(data);
		Logger.DEBUG(TAG, "[send] jObject: " + jObject);
		StringEntity se = null;
		try {
			se = new StringEntity(jObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] bytes = doCompress(se);
		Logger.DEBUG(TAG, "[send] bytes: " + bytes.length);
		String hex = mD5H(bytes);
		Logger.DEBUG(TAG, "[send] hex: " + hex);
		httpost.setHeader(X_ANDROID_ANALYZER_REPORT_MD5, hex);
		httpost.setHeader("Content-Encoding", "gzip");
		httpost.setEntity(new ByteArrayEntity(bytes));
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String response = httpclient.execute(httpost, responseHandler);
		return response;
	}

	private byte[] doCompress(StringEntity se) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedOutputStream bufos;
		byte[] retval = null;
		try {
			bufos = new BufferedOutputStream(new GZIPOutputStream(bos));
			se.writeTo(bufos);
			bufos.close();
			retval = bos.toByteArray();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retval;
	}
}
