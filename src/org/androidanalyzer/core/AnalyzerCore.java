/**
 * 
 */
package org.androidanalyzer.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.androidanalyzer.Constants;
import org.androidanalyzer.core.utils.Logger;
import org.androidanalyzer.transport.Reporter;
import org.androidanalyzer.transport.impl.json.HTTPJSONReporter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

/**
 * Core class for managing plugins and performing analysis
 * on the device.
 * 
 * @author k.raev
 */
public class AnalyzerCore {

  /**
   * String for Plugin Discovery broadcast intent
   */
  private static final String TAG = "Analyzer-Core";
  private static final int TIME_TO_WAIT_FOR_PLUGIN_CONNECTION = 3000;
  private static final int DEFAULT_MAX_TIME_TO_WAIT_FOR_PLUGIN_ANALYSIS_COMPLETION = 60000;
  private static final int DEFAULT_MIN_TIME_TO_WAIT_FOR_PLUGIN_ANALYSIS_COMPLETION = 10000;
  /* Context to be used for communication with the plugins */
  private Context ctx;
  /* UI callback for communication with the UI */
  private UICallback uiCallb;
  private static AnalyzerCore core;
  private Executor exec;

  private ArrayList<String> pluginCache = new ArrayList<String>();
  private Data report;
  private PluginServiceConnection runningPluginConn = null;
  private boolean stopAnalyzing = false;
  private boolean pluginAnalyzing = false;
  private Data tempReport = null;
  private UninstallBReceiver unRecv = null;

  /**
   * Initializes the Core
   * 
   * @param ctx
   *          Android Context for plugin communication
   */
  public void init(Context ctx) {
    AnalyzerCore.core = this;
    this.ctx = ctx;
    this.uiCallb = null;
    Intent regPluginsIntent = new Intent();
    regPluginsIntent.setAction(Constants.PLUGINS_DISCOVERY_INTENT);
    ctx.sendBroadcast(regPluginsIntent);
    exec = Executors.newFixedThreadPool(3);

    /*
     * registering braodcast receiver to handle uninstalled
     * external plugins
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
   * Starts Analysis in the Core. Each plugin is taken from
   * the registry Cache and called for analysis.
   * 
   * @return Data object containing report from all the
   *         plugins that were registered
   */
  public Data startAnalyzing() {
    Hashtable progressValues = null;
    if (uiCallb != null)
      progressValues = new Hashtable(5);
    cleanReport();
    Logger.DEBUG(TAG, "pluginCache : " + pluginCache);
    /* Updating UI on Analysis start */
    if (progressValues != null) {
      progressValues.put(UICallback.NUMBER_OF_PLUGINS, pluginCache.size());
      uiCallb.updateAnalysisProgress(progressValues);
    }
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
      runningPluginConn = null;
    }
    TelephonyManager TelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
    String deviceIMEI = TelephonyMgr.getDeviceId();
    if (deviceIMEI != null && report != null) {
      String md5 = Reporter.mD5H(deviceIMEI.getBytes());
      report.setString(Constants.MD5_IMEI, md5);
    } else {
      Logger.ERROR(TAG, "IMEI Check Failed");
    }
    Logger.DEBUG(TAG, "Core finished Analysis!");
    Logger.DEBUG(TAG, "Report : " + report);
    return report;
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
      // TODO make it with the Reporter interface in the
      // future
      HTTPJSONReporter reporter = new HTTPJSONReporter();
      return reporter.send(data, host);
    }
    return null;
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
      if (report == null)
        createReport();

      Object temp = report.getValue();
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
          report.setValue(data);
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
    report = new Data();
    try {
      report.setName(Constants.ROOT_DATA);
    } catch (Exception e) {
      Logger.ERROR(TAG, "Analyzer Core could not create report!");
    }
  }

  /**
   * Cleans main Core report
   */
  private void cleanReport() {
    report = null;
  }

  /**
   * Used to combine reports from different plugins
   * 
   * @param oldData
   *          Data object to be written into
   * @param newData
   *          new Data object(report) from which data to be
   *          taken and put into origData object
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
     * @see
     * org.androidanalyzer.core.IPluginRegistry#registerPlugin
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
   * Class used to return Registry class on connecton from
   * plugin
   */
  public static final class RegistryService extends Service {

    /*
     * (non-Javadoc)
     * @see
     * android.app.Service#onBind(android.content.Intent)
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
   * Returns new Registry object to be used for registering
   * plugin
   * 
   * @return new Registry Object
   */
  private Registry getRegistry() {
    return new Registry();
  }

  /**
   * Connects to plugin via aidl and returns
   * PluginServiceConnection for use
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
   * Helper class providing SericeConnection and
   * IAnalyzerPlugin interface to plugin
   */
  class PluginServiceConnection implements ServiceConnection {
    public IAnalyzerPlugin plugin = null;

    /*
     * (non-Javadoc)
     * @see
     * android.content.ServiceConnection#onServiceConnected
     * (android.content.ComponentName, android.os.IBinder)
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      plugin = (IAnalyzerPlugin.Stub.asInterface((IBinder) service));
      Logger.DEBUG(TAG, "Core Connected to plugin : " + name);
    }

    /*
     * (non-Javadoc)
     * @seeandroid.content.ServiceConnection#
     * onServiceDisconnected (android.content.ComponentName)
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
     * @see
     * android.content.BroadcastReceiver#onReceive(android
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
}
