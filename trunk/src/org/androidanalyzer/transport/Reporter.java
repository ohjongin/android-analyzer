package org.androidanalyzer.transport;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.androidanalyzer.core.Data;

/**
 * Abstract class representing sending data to the back-end.
 * 
 */

public abstract class Reporter {
	private static String HOST = null;
	private static String DEFAULT_HOST = "http://212.95.166.45:8080/DataServlet";
	

	/**
	 * Send data collected from available plugins to the server
	 * 
	 * @param data
	 *          Collected data
	 * @param host
	 *          Establish a connection to the specified host server
	 */
	abstract public Object send(Data dat1a, URL host) throws Exception;

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static String mD5H(byte[] bytes) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(bytes);
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void setHost(String host) {
		HOST = host;
	}

	public static String getHost() {
		HOST = HOST != null ? HOST : DEFAULT_HOST;
		return HOST;
	}
}
