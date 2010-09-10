package org.androidanalyzer.gui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.androidanalyzer.Constants;
import org.androidanalyzer.R;
import org.androidanalyzer.core.AnalyzerCore;
import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.IAnalyzerPlugin;
import org.androidanalyzer.core.PluginStatus;
import org.androidanalyzer.core.UICallback;
import org.androidanalyzer.core.utils.Logger;
import org.androidanalyzer.transport.Reporter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

/**
 * This is the main Activity of the Android Analyzer application.
 * It shows list with all registered AnalyzerPlugins, provided with
 * short information about their last run status. At the bottom end of the screen
 * is the "Analyze" button, that triggers the analysis.  
 *
 */
public class AnalyzerList extends Activity implements UICallback {
	
	private static final int MENU_SETTINGS = 0;
  private static final String TAG = "Analyzer-AnalyzerList";
  private static final String PREFS_NAME = "org.androidanalyzer.plugin.status";
  AnalyzerListAdapter adapter;
  ListView list;
	Hashtable<String, PluginStatus> name2status;
  ArrayList<PluginStatus> plugins;
  
  ProgressDialog progressDialog;
  int current = 0;
  ProgressHandler guiHandler;
  AnalyzerCore core;

  /** Called when the activity is first created. */
  /*
   * (non-Javadoc)
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    core = AnalyzerCore.getInstance();
    core.init(this);    
    core.setUICallback(this);
    setContentView(R.layout.main_list);
    list = (ListView)findViewById(R.id.plugins_list);
    name2status = new Hashtable<String, PluginStatus>();
    Context toUse = getApplicationContext();
    plugins = preparePluginList(toUse);
    adapter = new AnalyzerListAdapter(toUse, plugins, this);
    list.setAdapter(adapter);
    boolean debugEnabled = PreferencesManager.loadBooleanPreference(this, Constants.DEBUG);
    String host = PreferencesManager.loadStringPreference(this, Constants.HOST);
    if (host == null) {
      host = Reporter.getHost();
      PreferencesManager.savePreference(this, Constants.HOST, host);
    }
    Logger.setDebug(debugEnabled);
    Button analyzeB = (Button)findViewById(R.id.first_button);
    analyzeB.setText(R.string.analyze_button);
    analyzeB.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        guiHandler = new ProgressHandler(AnalyzerList.this);
        showDialog(Constants.PROGRESS_DIALOG);
        new Thread(new AnalyzingProcess(guiHandler, core)).start();
      }
    });
  }   
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onDestroy()
   */
  @Override
  protected void onDestroy() {
    core.clearCachedContent();
    super.onDestroy();
  }
  
	
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onPrepareOptionsMenu(android.view .Menu)
   */
  public boolean onPrepareOptionsMenu(Menu menu) {
    menu.clear();
    menu.add(0, MENU_SETTINGS, 0, R.string.menu_settings).setIcon(android.R.drawable.ic_menu_preferences);
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onOptionsItemSelected(android. view.MenuItem)
   */
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean result = false;
    switch (item.getItemId()) {
    case MENU_SETTINGS:
      Intent intent = new Intent(AnalyzerList.this, AnalyzerSettings.class);
      startActivity(intent);
      result = true;
      break;
    }
    return result || super.onOptionsItemSelected(item);
  }  
	
  /*
   * (non-Javadoc)
   * @see android.app.Activity#onCreateDialog(int)
   */
  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
      case Constants.PROGRESS_DIALOG:
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(getString(R.string.progress_dialog_title));
        progressDialog.setMessage(getString(R.string.progress_dialog_msg));
        return progressDialog;
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.androidanalyzer.core.UICallback#updateAnalysisProgress(java.util.Hashtable)
   */
  @Override
  public void updateAnalysisProgress(Hashtable progress) {
    if (progress.containsKey(UICallback.PLUGIN_STARTED_ANALYZING)) {
      String name = (String) progress.get(UICallback.NAME_OF_PLUGIN);
      int total = (Integer) progress.get(UICallback.NUMBER_OF_PLUGINS);
      Message msg = guiHandler.obtainMessage();
      Bundle bundle = new Bundle();
      bundle.putInt(Constants.GUI_HANDLER_PROGRESS_TOTAL_PLUGINS, total);
      current++;
      bundle.putInt(Constants.GUI_HANDLER_PROGRESS_CURRENT_PLUGIN, current);
      bundle.putString(Constants.GUI_HANDLER_PROGRESS_PLUGIN_NAME, name);
      bundle.putString(Constants.GUI_HANDLER_PROGRESS, Constants.GUI_HANDLER_PROGRESS_ENABLED);
      msg.setData(bundle);
      guiHandler.sendMessage(msg);
    }
  }
  
  /*
   * (non-Javadoc)
   * @see org.androidanalyzer.core.UICallback#notifyPluginRegistered(org.androidanalyzer.core.IAnalyzerPlugin)
   */
  @Override
  public void notifyPluginRegistered(IAnalyzerPlugin iAnalyzerPlugin) {
    try {
      String pluginClass = iAnalyzerPlugin.getClassName();
      SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
      String status = prefs.getString(pluginClass, null);
      String description = iAnalyzerPlugin.getDescription();
      PluginStatus decoded = null;
      String name = iAnalyzerPlugin.getName();
      if (status == null) {
        decoded = new PluginStatus(name, pluginClass, PluginStatus.STATUS_NOT_RUN, -1, description);
      } else {
        decoded = PluginStatus.decodeStatus(status);
        decoded.setPluginName(name);
        decoded.setPluginDescription(description);
      }
      if (decoded != null) {
        String encoded = PluginStatus.encodeStatus(decoded);
        if (encoded != null) {
          Editor edit = prefs.edit();
          edit.putString(pluginClass, encoded);
          edit.commit();
        }
        PluginStatus old = name2status.get(pluginClass);
        if (old != null) {
          plugins.remove(old);
       }
        plugins.add(decoded);
        name2status.put(pluginClass, decoded);
        adapter.notifyDataSetChanged();
//        adapter.listItems = plugins;
//        list.setAdapter(adapter);
      }
    } catch (RemoteException e) {
      Logger.ERROR(TAG, "Error handling plugin registered: "+e.getMessage());
    }
    
  }

  /*
   * (non-Javadoc)
   * @see org.androidanalyzer.core.UICallback#notifyPluginUnregistered(org.androidanalyzer.core.IAnalyzerPlugin)
   */
  @Override
  public void notifyPluginUnregistered(IAnalyzerPlugin iAnalyzerPlugin) {
    try {
      String pluginClass = iAnalyzerPlugin.getClassName();
      SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
      String status = prefs.getString(pluginClass, null);
      if (status != null) {
        PluginStatus decoded = PluginStatus.decodeStatus(status);
        if (decoded != null) {
          plugins.remove(decoded);
          name2status.remove(pluginClass);
        }
      }
    } catch (RemoteException e) {
      Logger.ERROR(TAG, "Error handling plugin unregistered: "+e.getMessage());
    }
  }
	
  void updateProgress(int total, int current, String pluginName) {
    progressDialog.setMax(total);
    progressDialog.setProgress(0);
    progressDialog.incrementProgressBy(current);
    progressDialog.setMessage(getString(R.string.progress_dialog_msg)+pluginName);
  }
  
  void hideProgress(Data result) {
    progressDialog.setProgress(0);
    dismissDialog(Constants.PROGRESS_DIALOG);
    current = 0;
    Intent intent = new Intent(this, ReportActivity.class);
    String host = PreferencesManager.loadStringPreference(this, Constants.HOST);
    intent.putExtra(Constants.GUI_HANDLER_SEND, result);
    intent.putExtra(Constants.HOST, host);
    startActivity(intent);
  }
  
  ArrayList<PluginStatus> preparePluginList(Context ctx) {
  	ArrayList<PluginStatus> plugins = new ArrayList<PluginStatus>();
  	SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, 0);
  	Map<String, ?> all = prefs.getAll();
  	Set<?> values = all.entrySet();
    Iterator<?> it = values.iterator();
    Entry<String, String> record;
    PluginStatus decoded;
    for (;it.hasNext();) {
      record = (Entry<String, String>)it.next();
      decoded = PluginStatus.decodeStatus(record.getValue());        
      if (decoded != null) {
        plugins.add(decoded);
        name2status.put(decoded.getPluginClass(), decoded);
      }
    }
  	return plugins;
  }
}