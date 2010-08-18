package org.androidanalyzer.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.androidanalyzer.Constants;
import org.androidanalyzer.core.utils.Logger;
import org.androidanalyzer.transport.Reporter;
import org.androidanalyzer.transport.impl.json.HTTPJSONReporter;
import org.androidanalyzer.transport.impl.json.JSONFormatter;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

/**
 * Core class for managing plugins and performing analysis on the device.
 * 
 * @author k.raev
 */
public class AnalyzerCore {

	private static final String VERSION = "1.0.0";
	private static final String TAG = "Analyzer-Core";
	private static final int TIME_TO_WAIT_FOR_PLUGIN_CONNECTION = 3000;
	private static final int DEFAULT_MAX_TIME_TO_WAIT_FOR_PLUGIN_ANALYSIS_COMPLETION = 60000;
	private static final int DEFAULT_MIN_TIME_TO_WAIT_FOR_PLUGIN_ANALYSIS_COMPLETION = 10000;
	private static AnalyzerCore core;

	/* Context to be used for communication with the plugins */
	private Context ctx;
	/* UI callback for communication with the UI */
	private UICallback uiCallb;
	private Executor exec;

	private ArrayList<String> pluginCache = new ArrayList<String>();
	private PluginServiceConnection runningPluginConn = null;
	private boolean stopAnalyzing = false;
	private boolean pluginAnalyzing = false;
	private Data root = null;
	private Data reportPlugins = null;
	private Data reportMetadata = null;
	private Data tempReport = null;
	private UninstallBReceiver unRecv = null;
	
	public static AnalyzerCore getInstance() {
	  if (core == null) 
	    core = new AnalyzerCore();
	  return core;
	}
	private PluginStatus pluginStatus = null;

	/**
	 * Initializes the Core
	 * 
	 * @param ctx
	 *          Android Context for plugin communication
	 */
	public void init(Context ctx) {
//		AnalyzerCore.core = this;
		this.ctx = ctx;
		this.uiCallb = null;
		Intent regPluginsIntent = new Intent();
		regPluginsIntent.setAction(Constants.PLUGINS_DISCOVERY_INTENT);
		ctx.sendBroadcast(regPluginsIntent);
		exec = Executors.newFixedThreadPool(3);

		/*
		 * registering braodcast receiver to handle uninstalled external plugins
		 */
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		unRecv = new UninstallBReceiver();
		ctx.registerReceiver(unRecv, filter);
	}

	/**
	 * Sets UI callback for sending updates to the UI
	 * 
	 * @param uiCallb
	 *          UI object implementing UICallback class
	 */
	public void setUICallback(UICallback uiCallb) {
		this.uiCallb = uiCallb;
	}

	/**
	 * Can be called to clear cached content from the Core
	 */
	public void clearCachedContent() {
		if (unRecv != null) {
			ctx.unregisterReceiver(unRecv);
		}
	}

	/**
	 * Starts Analysis in the Core. Each plugin is taken from the registry Cache
	 * and called for analysis.
	 * 
	 * @return Data object containing report from all the plugins that were
	 *         registered
	 */
	public Data startAnalyzing() {
		Hashtable progressValues = null;
		int pluginCounter = 0;
		Data plugins = null;
		String status = null;
		if (uiCallb != null)
			progressValues = new Hashtable(5);
		cleanReport();
		Logger.DEBUG(TAG, "pluginCache : " + pluginCache);
		/* Updating UI on Analysis start */
		if (progressValues != null) {
			progressValues.put(UICallback.NUMBER_OF_PLUGINS, pluginCache.size());
			uiCallb.updateAnalysisProgress(progressValues);
		}
		pluginStatus = new PluginStatus(ctx);
		if (pluginCache != null) {
			for (String plugin : pluginCache) {
				runningPluginConn = connectToPlugin(plugin);
				if (runningPluginConn != null && runningPluginConn.plugin != null) {
					try {
						runningPluginConn.plugin.setDebugEnabled(Logger.getDebug());
					} catch (RemoteException exp) {
						Logger.DEBUG(TAG, "Error while trying to activate debug for plugin : " + plugin);
					}
					try {
						/* Updating UI on plugin Analysis start */
						if (progressValues != null) {
							progressValues.remove(UICallback.PLUGIN_FAILED);
							progressValues.remove(UICallback.PLUGIN_FINISHED_ANALYZING);
							progressValues.put(UICallback.NAME_OF_PLUGIN, runningPluginConn.plugin.getName());
							progressValues.put(UICallback.PLUGIN_STARTED_ANALYZING, true);
							uiCallb.updateAnalysisProgress(progressValues);
						}
					} catch (RemoteException e1) {
					}
					long timeout = DEFAULT_MAX_TIME_TO_WAIT_FOR_PLUGIN_ANALYSIS_COMPLETION;
					try {
						timeout = runningPluginConn.plugin.getTimeout();
					} catch (RemoteException e) {
						Logger.ERROR(TAG, "Could not get timeout for plugin!");
						timeout = DEFAULT_MAX_TIME_TO_WAIT_FOR_PLUGIN_ANALYSIS_COMPLETION;
					}
					if (timeout > DEFAULT_MAX_TIME_TO_WAIT_FOR_PLUGIN_ANALYSIS_COMPLETION)
						timeout = DEFAULT_MAX_TIME_TO_WAIT_FOR_PLUGIN_ANALYSIS_COMPLETION;
					if (timeout <= 100)
						timeout = DEFAULT_MIN_TIME_TO_WAIT_FOR_PLUGIN_ANALYSIS_COMPLETION;
					tempReport = null;
					pluginAnalyzing = true;
					exec.execute(new PluginAnalysisHandler(runningPluginConn.plugin, progressValues));
					while (pluginAnalyzing == true && tempReport == null && timeout > 0) {
						try {
							Thread.sleep(100);
							timeout -= 100;
						} catch (InterruptedException e) {
							Logger.ERROR(TAG, "Could not Sleep thread in Core!");
						}
					}
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
					Date currentTime = new Date();
					String dateString = formatter.format(currentTime);
					try {
						status = runningPluginConn.plugin.getStatus();
					} catch (RemoteException e1) {
						Logger.ERROR(TAG, "Failed to get for PluginInfo", e1);
					}
					pluginStatus.constructPluginStatus(plugin, status, dateString);
					if (plugins == null) {
						plugins = new Data();
						try {
							plugins.setName(Constants.METADATA_PLUGINS);
						} catch (Exception e) {
							Logger.ERROR(TAG, "Could not set Metadata!", e);
						}
					}
					Data currentPlugin = getPluginMetaData(runningPluginConn.plugin, pluginCounter++);
					try {
						plugins.setValue(currentPlugin);
					} catch (Exception e) {
						Logger.ERROR(TAG, "Could not set Metadata!", e);
					}
					ctx.unbindService(runningPluginConn);
					if (tempReport != null) {
						addDataToReport(tempReport);
					} else {
						Logger.DEBUG(TAG, "tempReport : " + tempReport);
					}
				}
				/* Updating UI on plugin Analysis finish */
				if (progressValues != null) {
					progressValues.remove(UICallback.PLUGIN_STARTED_ANALYZING);
					progressValues.put(UICallback.PLUGIN_FINISHED_ANALYZING, true);
					uiCallb.updateAnalysisProgress(progressValues);
				}
				if (stopAnalyzing) {
					stopAnalyzing = false;
					break;
				}
			}
			if (reportMetadata == null) {
				reportMetadata = new Data();
				try {
					reportMetadata.setName(Constants.ROOT_METADATA);
				} catch (Exception e) {
					Logger.ERROR(TAG, "Could not set Metadata name!", e);
				}
			}
			if (plugins != null) {
				try {
					reportMetadata.setValue(plugins);
				} catch (Exception e) {
					Logger.ERROR(TAG, "Could not set Metadata!", e);
				}
			}
			runningPluginConn = null;
		}

		/* Create Metadata - Device ID */
		TelephonyManager TelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceIMEI = TelephonyMgr.getDeviceId();
		Data device = new Data();
		try {
			device.setName(Constants.METADATA_DEVICE);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Metadata!", e);
		}
		if (deviceIMEI != null && reportPlugins != null) {
			String md5 = Reporter.mD5H(deviceIMEI.getBytes());
			// report.setString(Constants.MD5_IMEI, md5);
			Data imei = new Data();
			try {
				imei.setName(Constants.METADATA_DEVICE_ID);
				imei.setValue(md5);
				imei.setValueMetric(Constants.METADATA_DEVICE_ID_METRIC);
				device.setValue(imei);
				reportMetadata.setValue(device);
			} catch (Exception e) {
				Logger.ERROR(TAG, "Could not set Metadata!", e);
			}
		} else {
			Logger.ERROR(TAG, "IMEI Check Failed");
		}
		/* Create Metadata - Analyzer Version */
		try {
			Data analyzerVersion = new Data();
			analyzerVersion.setName(Constants.METADATA_ANALYZER);
			Data version = new Data();
			version.setName(Constants.METADATA_ANALYZER_VERSION);
			version.setValue(VERSION);
			analyzerVersion.setValue(version);
			reportMetadata.setValue(analyzerVersion);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Metadata!", e);
		}

		/* Create Metadata - Date and Time */
		try {
			Data date = new Data();
			date.setName(Constants.METADATA_DATE);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date currentTime = new Date();
			String dateString = formatter.format(currentTime);
			date.setValue(dateString);
			date.setValueMetric(Constants.METADATA_DATE_METRIC);
			reportMetadata.setValue(date);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Metadata!", e);
		}
		Logger.DEBUG(TAG, "Core finished Analysis!");
		Logger.DEBUG(TAG, "Report : " + reportPlugins);
		try {
			if (root == null)
				root = new Data();
			root.setName(Constants.ROOT);
			root.setValue(reportPlugins);
			root.setValue(reportMetadata);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not create final report!", e);
		}
		for (String plugin : pluginCache) {
			Logger.DEBUG(TAG, "Clas name: " + plugin);
		}
		return root;
	}

	/**
	 * Stops ongoing Analysis from the Core
	 */
	public void stopAnalyzing() {
		stopAnalyzing = true;
		synchronized (runningPluginConn) {
			if (runningPluginConn != null && runningPluginConn.plugin != null)
				try {
					runningPluginConn.plugin.stopAnalysis();
				} catch (RemoteException e) {
					Logger.ERROR(TAG, "Plugin disconected during stopAnalysis", e);
				}
		}
	}

	/**
	 * Sends report from analysis to server
	 * 
	 * @throws Exception
	 */
	public Object sendReport(Data data, URL host) throws Exception {
		if (data != null && host != null) {
			HTTPJSONReporter reporter = new HTTPJSONReporter();
			return reporter.send(data, host);
		}
		return null;
	}

	/**
	 * Save report to local file
	 * 
	 * @return fName - Saved file name of the report
	 */
	public String writeToFile(Data data) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		String fName = null;
		JSONObject jObject = JSONFormatter.format(data);
		Logger.DEBUG(TAG, "[send] jObject: " + jObject);
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
			Date currentTime = new Date();
			String dateString = formatter.format(currentTime);
			fName = Constants.FILE_NAME + "-" + dateString + ".txt";
			File root = Environment.getExternalStorageDirectory();
			if (root != null && root.canWrite()) {
				Logger.DEBUG(TAG, "SD card is available");
				File sdfile = new File(root, fName);
				fos = new FileOutputStream(sdfile);
				fName = " sdcard/" + fName;
			} else {
				Logger.DEBUG(TAG, "SD card is not available");
				fos = ctx.openFileOutput(fName, ctx.MODE_PRIVATE);
				fName = " data/data/org.androidanalyzer/files/" + fName;
			}
			osw = new OutputStreamWriter(fos);
			osw.write(jObject.toString());
			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				osw.close();
				fos.close();
			} catch (IOException e) {
				Logger.ERROR(TAG, "Failed to close streams!");
			}
		}
		return fName;
	}

	public PluginStatus getPluginInfo() {
		return pluginStatus;
	}

	/**
	 * Adds data to main Report in Core
	 * 
	 * @param data
	 *          to be added
	 */
	private void addDataToReport(Data data) {

		if (data != null) {
			Logger.DEBUG(TAG, "addDataToReport for plugin : " + data.getName());
			if (reportPlugins == null)
				createReport();

			Object temp = reportPlugins.getValue();
			ArrayList<Data> list = null;
			if (temp instanceof ArrayList<?>) {
				list = (ArrayList<Data>) temp;
			}
			if (list != null) {
				for (Data reportedData : list) {
					if (reportedData.getName() != null && data.getName() != null && reportedData.getName().equals(data.getName())) {
						combineReports(reportedData, data);
						return;
					}
				}
			}
			if (data.getName() != null && data.getValue() != null)
				try {
					reportPlugins.setValue(data);
				} catch (Exception e) {
					Logger.ERROR(TAG, "Analyzer Core : could not attach to report data : " + data.getName(), e);
				}
		} else {
			Logger.ERROR(TAG, "Called addDataToReport with data : " + data);
		}

	}

	/**
	 * creates main report in Core
	 */
	private void createReport() {
		reportPlugins = new Data();
		try {
			reportPlugins.setName(Constants.ROOT_DATA);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Analyzer Core could not create report!");
		}
	}

	/**
	 * Cleans main Core report
	 */
	private void cleanReport() {
		root = null;
		reportPlugins = null;
		reportMetadata = null;
		tempReport = null;
		pluginStatus = null;
	}

	/**
	 * Used to combine reports from different plugins
	 * 
	 * @param oldData
	 *          Data object to be written into
	 * @param newData
	 *          new Data object(report) from which data to be taken and put into
	 *          origData object
	 */
	private void combineReports(Data oldData, Data newData) {
		if (oldData != null && newData != null) {
			if (oldData.getValue() instanceof String && newData.getValue() instanceof String) {
				try {
					oldData.setValue(newData.getValue());
				} catch (Exception e) {
					Logger.DEBUG(TAG, "AC : combineReports could not set data as value!");
				}
			}
			if (oldData.getValue() instanceof String && newData.getValue() instanceof ArrayList<?>
					|| oldData.getValue() instanceof ArrayList<?> && newData.getValue() instanceof String) {
				Logger.ERROR(TAG, "combineReports() Data objects with different value types!");
			}
			if (oldData.getValue() instanceof ArrayList<?> && newData.getValue() instanceof ArrayList<?>) {
				ArrayList<Data> oldList = (ArrayList<Data>) oldData.getValue();
				ArrayList<Data> newList = (ArrayList<Data>) newData.getValue();
				for (Data data : newList) {
					Data oldDataFound = null;
					for (Data oData : oldList) {
						if (oData.getName().equals(data.getName())) {
							oldDataFound = oData;
							break;
						}
					}
					if (oldDataFound != null) {
						combineReports(oldDataFound, newData);
					} else {
						try {
							oldData.setValue(data);
						} catch (Exception e) {
							Logger.ERROR(TAG, "AC : combineReports could not set Data as value!", e);
						}
					}
				}
			}
		}
	}

	/**
	 * Class implementation of IPluginRegistry aidl interface
	 */
	public class Registry extends IPluginRegistry.Stub {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.androidanalyzer.core.IPluginRegistry#registerPlugin
		 * (org.androidanalyzer.core.IAnalyzerPlugin)
		 */
		@Override
		public void registerPlugin(String pluginClass) throws RemoteException {
			if (pluginClass != null && (pluginCache == null || !pluginCache.contains(pluginClass))) {
				if (pluginCache == null) {
					pluginCache = new ArrayList<String>();
				}
				pluginCache.add(pluginClass);
			}
			Logger.DEBUG(TAG, "registered plugin : " + pluginClass);
		}

	}

	/**
	 * Class used to return Registry class on connecton from plugin
	 */
	public static final class RegistryService extends Service {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.app.Service#onBind(android.content.Intent)
		 */
		@Override
		public IBinder onBind(Intent intent) {
			Logger.DEBUG(TAG, "onBind in RegistryService");
			if (IPluginRegistry.class.getName().equals(intent.getAction())) {
				Logger.DEBUG(TAG, "Returning Registry interface in Core");
				if (AnalyzerCore.core != null)
					return AnalyzerCore.core.getRegistry();
				return null;
			}
			return null;
		}

	}

	/**
	 * Returns new Registry object to be used for registering plugin
	 * 
	 * @return new Registry Object
	 */
	private Registry getRegistry() {
		return new Registry();
	}

	/**
	 * Connects to plugin via aidl and returns PluginServiceConnection for use
	 * 
	 * @param plugin
	 *          Plugin identity string for bindService
	 * @return
	 */
	private PluginServiceConnection connectToPlugin(String pluginClass) {

		PluginServiceConnection conn = new PluginServiceConnection();

		boolean connSuccess = ctx.bindService(new Intent(pluginClass), conn, Context.BIND_AUTO_CREATE);
		int timeToWait = TIME_TO_WAIT_FOR_PLUGIN_CONNECTION;
		while (conn.plugin == null && timeToWait > 0) {
			// Logger.DEBUG(TAG, "plugin connection : " +
			// conn.plugin);
			timeToWait -= 100;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		return conn;
	}

	/**
	 * Helper class providing SericeConnection and IAnalyzerPlugin interface to
	 * plugin
	 */
	class PluginServiceConnection implements ServiceConnection {
		public IAnalyzerPlugin plugin = null;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.content.ServiceConnection#onServiceConnected
		 * (android.content.ComponentName, android.os.IBinder)
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			plugin = (IAnalyzerPlugin.Stub.asInterface((IBinder) service));
			uiCallb.notifyPluginRegistered(plugin);
			Logger.DEBUG(TAG, "Core Connected to plugin : " + name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeandroid.content.ServiceConnection# onServiceDisconnected
		 * (android.content.ComponentName)
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

	};

	class PluginAnalysisHandler implements Runnable {

		private IAnalyzerPlugin plugin;
		private Hashtable progressValues;

		/**
     * 
     */
		public PluginAnalysisHandler(IAnalyzerPlugin plugin, Hashtable progressValues) {
			this.plugin = plugin;
			this.progressValues = progressValues;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			String pluginName = null;
			try {
				pluginName = plugin.getName();
				tempReport = plugin.startAnalysis();
			} catch (RemoteException e) {

				if (progressValues != null) {
					progressValues.remove(UICallback.PLUGIN_STARTED_ANALYZING);
					progressValues.put(UICallback.PLUGIN_FAILED, true);
					uiCallb.updateAnalysisProgress(progressValues);
				}
				Logger.ERROR(TAG, "Plulgin disconected durring analysis!", e);
			} finally {
				if (tempReport == null) {
					try {
						if (pluginName != null && pluginName.length() > 0) {
							tempReport = new Data();
							tempReport.setName(pluginName);
							tempReport.setStatus(Constants.NODE_STATUS_FAILED);
						}
					} catch (Exception e1) {
						Logger.ERROR(TAG, "Could create dummy node for failed Plugin!", e1);
						tempReport = null;
					}
				}
				pluginAnalyzing = false;
			}
		}
	}

	class UninstallBReceiver extends BroadcastReceiver {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.content.BroadcastReceiver#onReceive(android
		 * .content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			String pkg = intent.getDataString().substring(8);
			Logger.DEBUG(TAG, "uninstalled package : " + pkg);
			for (String plugin : pluginCache) {
				if (plugin.contains(pkg)) {
					pluginCache.remove(plugin);
					break;
				}
			}
		}

	}

	private Data getPluginMetaData(IAnalyzerPlugin plugin, int counter) {
		Data currentPlugin = new Data();
		Data currentPluginName = new Data();
		Data currentPluginClassName = new Data();
		Data currentPluginVersion = new Data();
		Data currentPluginVendor = new Data();
		Data currentPluginStatus = new Data();
		try {
			currentPlugin.setName(Constants.METADATA_PLUGIN_ + counter);

			currentPluginName.setName(Constants.METADATA_PLUGIN_HUMAN_NAME);
			currentPluginName.setValue(plugin.getName());
			currentPlugin.setValue(currentPluginName);

			currentPluginClassName.setName(Constants.METADATA_PLUGIN_CLASS_NAME);
			currentPluginClassName.setValue(plugin.getClassName());
			currentPlugin.setValue(currentPluginClassName);

			currentPluginVersion.setName(Constants.METADATA_PLUGIN_VERSION);
			currentPluginVersion.setValue(plugin.getVersion());
			currentPlugin.setValue(currentPluginVersion);

			currentPluginVendor.setName(Constants.METADATA_PLUGIN_VENDOR);
			currentPluginVendor.setValue(plugin.getVendor());
			currentPlugin.setValue(currentPluginVendor);

			currentPluginStatus.setName(Constants.METADATA_PLUGIN_STATUS);
			String status = plugin.getStatus();
			if (status.equals(Constants.METADATA_PLUGIN_STATUS_PASSED)) {
				currentPluginStatus.setValue(plugin.getStatus());
			} else {
				Data currentPluginStatusDescription = new Data();
				currentPluginStatusDescription.setName(Constants.METADATA_PLUGIN_STATUS_DESCRIPTION);
				currentPluginStatusDescription.setValue(plugin.getStatus());
				currentPluginStatus.setValue(currentPluginStatusDescription);
			}
			currentPlugin.setValue(currentPluginStatus);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set current plugin data!", e);
		}
		return currentPlugin;
	}
}
