/**
 * 
 */
package org.androidanalyzer.plugins.memory;

import org.androidanalyzer.core.utils.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast Receiver to fire up the plugin registering it to the Core
 * 
 * @author k.raev
 *
 */
public class BReceiver extends BroadcastReceiver {
	
	private static final String TAG = "Analyzer-MemoryPlugin-BReceiver";
  /* (non-Javadoc)
   * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
   */
  @Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, MemoryPlugin.class));
		Logger.DEBUG(TAG, "Plugin broadcast received for " + MemoryPlugin.class);
	}

}