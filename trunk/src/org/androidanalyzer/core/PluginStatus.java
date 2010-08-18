package org.androidanalyzer.core;

import org.androidanalyzer.core.utils.Logger;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Description
 * 
 * @author <Georgi Chepilev>
 * @version <1.0.0>
 */
public class PluginStatus {
	private static final String TAG = "Analyzer-Core";
	private static final String PREFS_NAME = "org.androidanalyzer.core.PluginStatus";
	private static final String SEPARATOR = ":";
	private Context ctx;

	public PluginStatus(Context ctx) {
		this.ctx = ctx;
	}

	/**
	 * Return plugin status
	 * 
	 * @param className
	 *          Plugin class name
	 * @return status Return status for the given class name of the plugin. Value
	 *         could be "Passed" or "Failed"
	 */
	public String getStatus(String className) {
		if (className == null || className.length() == 0) {
			Logger.ERROR(TAG, "className is null");
			return null;
		}
		SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		String allInfo = prefs.getString(className, null);
		Logger.DEBUG(TAG, "Get all info for " + className + ":" + allInfo);
		String status = allInfo.substring(allInfo.indexOf(SEPARATOR) + 1);
		Logger.DEBUG(TAG, "Plugin status: " + status);
		return status;
	}

	/**
	 * Return last run time of the given plugin
	 * 
	 * @param className
	 *          Plugin class name
	 * @return Return last run time for the given class name of the plugin. Value
	 *         is in format yyyy_MM_dd-HH_mm_ss
	 */
	public String getLastRun(String className) {
		if (className == null || className.length() == 0) {
			Logger.ERROR(TAG, "className is null");
			return null;
		}
		SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
		String allInfo = prefs.getString(className, null);
		Logger.DEBUG(TAG, "Get all info for " + className + ":" + allInfo);
		String lastRun = allInfo.substring(0, allInfo.indexOf(SEPARATOR));
		Logger.DEBUG(TAG, "Plugin lastRun: " + lastRun);
		return lastRun;
	}

	/**
	 * 
	 * @param className
	 * @param status
	 * @param lastRun
	 */
	public void constructPluginStatus(String className, String status, String lastRun) {
		if (className == null || className.length() == 0) {
			Logger.ERROR(TAG, "className is null");
		}
		if (status == null || status.length() == 0) {
			Logger.ERROR(TAG, "status is null");
		}
		if (lastRun == null || lastRun.length() == 0) {
			Logger.ERROR(TAG, "lastRun is null");
		}
		SharedPreferences.Editor prefs = ctx.getSharedPreferences(PREFS_NAME, 0).edit();
		Logger.DEBUG(TAG, "Store [className - status" + SEPARATOR + "lastRun ] -" + className + " - " + status + SEPARATOR + lastRun);
		prefs.putString(className, status + SEPARATOR + lastRun);
		prefs.commit();
	}

}
