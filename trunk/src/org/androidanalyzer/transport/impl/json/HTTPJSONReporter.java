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
 * Description
 * 
 * @author <Georgi Chepilev>
 * @version <1.0.0>
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
		ResponseHandler responseHandler = new BasicResponseHandler();
		// try {
		HttpResponse response = (HttpResponse) httpclient.execute(httpost, responseHandler);
		return HttpHelper.request(response);
		/*
		 * Log.d(tag, "Response: " + response); } catch (HttpResponseException hre)
		 * { int code = hre.getStatusCode(); Log.d(tag, "Status code: " + code); }
		 * catch (ClientProtocolException e) { e.printStackTrace(); } catch
		 * (IOException e) { e.printStackTrace(); }
		 */

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

	static class HttpHelper {
		static String request(HttpResponse response) {
			String result = "";
			try {
				InputStream in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder str = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					str.append(line + System.getProperty("line.separator"));
				}
				in.close();
				result = str.toString();
			} catch (Exception ex) {
				result = "Error";
			}
			return result;
		}
	}

}
