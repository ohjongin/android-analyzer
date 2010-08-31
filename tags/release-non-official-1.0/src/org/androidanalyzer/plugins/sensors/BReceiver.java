package org.androidanalyzer.plugins.sensors;

import org.androidanalyzer.core.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Description
 * 
 * @author <Georgi Chepilev>
 * @version <1.0.0>
 */
public class BReceiver extends BroadcastReceiver {

	private static final String TAG = "Analyzer-SensorsPlugin-BReceiver";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, SensorsPlugin.class));
		Logger.DEBUG(TAG, "Plugin broadcast received for " + SensorsPlugin.class);
	}

}
