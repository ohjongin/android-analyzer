package org.androidanalyzer.gui;

import java.util.ArrayList;
import java.util.Hashtable;

import org.androidanalyzer.Constants;
import org.androidanalyzer.R;
import org.androidanalyzer.core.AnalyzerCore;
import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.IAnalyzerPlugin;
import org.androidanalyzer.core.PluginStatus;
import org.androidanalyzer.core.UICallback;
import org.androidanalyzer.core.utils.Logger;

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

public class AnalyzerList extends Activity implements UICallback {
	
	private static final int MENU_SETTINGS = 0;
  private static final String TAG = "AnalyzerList";
  private static final String PREFS_NAME = "org.androidanalyzer.plugin.status";
  AnalyzerListAdapter adapter;
  ListView list;
	Hashtable<String, Boolean> name2status;
	Hashtable<String, String> name2lastRun;
  ArrayList<PluginStatus> plugins;
  
  ProgressDialog progressDialog;
  int current = 0;
  ProgressHandler guiHandler;
  AnalyzerCore core;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    core = AnalyzerCore.getInstance();
    core.init(this);    
    core.setUICallback(this);
    setContentView(R.layout.main_list);
    list = (ListView)findViewById(R.id.plugins_list);
    name2status = new Hashtable<String, Boolean>();
    name2lastRun = new Hashtable<String, String>();
    Context toUse = getApplicationContext();
    plugins = new ArrayList<PluginStatus>();
    adapter = new AnalyzerListAdapter(toUse, plugins, this);
    list.setAdapter(adapter);
    boolean debugEnabled = PreferencesManager.loadBooleanPreference(this, Constants.DEBUG);
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
  

//  ArrayList<AbstractPlugin> preparePluginList(Context ctx) {
//    	ArrayList<AbstractPlugin> plugins = new ArrayList<AbstractPlugin>();
//    	Calendar cal = Calendar.getInstance();
//    	cal.set(2010, 7, 1, 16, 12);
//    	Date time = cal.getTime();
//    	String lastRun = "Last run "+formatDate.format(time) + " at "+formatTime.format(time);
//    	AbstractPlugin plugin = new APIPlugin();
//    	name2status.put(plugin.getPluginName(), Boolean.FALSE);
//    	name2lastRun.put(plugin.getPluginName(), lastRun);
//    	plugins.add(plugin);
//    	plugin = new CameraPlugin();
//      name2status.put(plugin.getPluginName(), Boolean.TRUE);
//      name2lastRun.put(plugin.getPluginName(), lastRun);
//    	plugins.add(plugin);
//    	plugin = new CPUPlugin();
//      name2status.put(plugin.getPluginName(), Boolean.TRUE);
//      name2lastRun.put(plugin.getPluginName(), lastRun);
//    	plugins.add(plugin);
//    	plugin = new DisplayPlugin();
//      name2status.put(plugin.getPluginName(), Boolean.FALSE);
//      name2lastRun.put(plugin.getPluginName(), "Not Run");
//    	plugins.add(plugin);
//    	plugin = new LocationPlugin();
//      name2status.put(plugin.getPluginName(), Boolean.FALSE);
//      name2lastRun.put(plugin.getPluginName(), lastRun);
//    	plugins.add(plugin);
//    	plugin = new MemoryPlugin();
//      name2status.put(plugin.getPluginName(), Boolean.TRUE);
//      name2lastRun.put(plugin.getPluginName(), lastRun);
//    	plugins.add(plugin);
//    	return plugins;
//    }
	
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

  @Override
  public void updateAnalysisProgress(Hashtable progress) {
    if (progress.containsKey(UICallback.PLUGIN_STARTED_ANALYZING)) {
      String name = (String) progress.get(UICallback.NAME_OF_PLUGIN);
      int total = (Integer) progress.get(UICallback.NUMBER_OF_PLUGINS);
      Message msg = guiHandler.obtainMessage();
      Bundle bundle = new Bundle();
      bundle.putInt("total", total);
      current++;
      bundle.putInt("current", current);
      bundle.putString("name", name);
      bundle.putString(Constants.HANDLER_PROGRESS, "true");
      msg.setData(bundle);
      guiHandler.sendMessage(msg);
    }
  }
  
  void updateProgress(int total, int current, String pluginName) {
    System.out.println("UPDATE PROGRESS: "+total+" CURRENT: "+current);
    if (total != -1)
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
    intent.putExtra(Constants.HANDLER_SEND, result);
    intent.putExtra(Constants.HOST, host);
    startActivity(intent);
  }

  @Override
  public void notifyPluginRegistered(IAnalyzerPlugin iAnalyzerPlugin) {
    try {
      String pluginClass = iAnalyzerPlugin.getClassName();
      System.out.println("Plugin registered: "+pluginClass);
      SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
      String status = prefs.getString(pluginClass, null);
      PluginStatus decoded = null;
      if (status == null) {
        String name = iAnalyzerPlugin.getName();
        decoded = new PluginStatus(name, pluginClass, PluginStatus.STATUS_NOT_RUN, -1);
        String encoded = PluginStatus.encodeStatus(decoded);
        if (encoded != null) {
          Editor edit = prefs.edit();
          edit.putString(pluginClass, encoded);
          edit.commit();
        }
      } else {
        decoded = PluginStatus.decodeStatus(status);
      }
      if (decoded != null) {
        plugins.add(decoded);
        adapter.listItems = plugins;
        list.setAdapter(adapter);
      }
    } catch (RemoteException e) {
      Logger.ERROR(TAG, "Error handling plugin registered: "+e.getMessage());
    }
    
  }

  @Override
  public void notifyPluginUnregistered(IAnalyzerPlugin iAnalyzerPlugin) {
    try {
      String pluginClass = iAnalyzerPlugin.getClassName();
      SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
      String status = prefs.getString(pluginClass, null);
      if (status != null) {
        PluginStatus decoded = PluginStatus.decodeStatus(status);
        if (decoded != null)
          plugins.remove(decoded);
      }
    } catch (RemoteException e) {
      Logger.ERROR(TAG, "Error handling plugin unregistered: "+e.getMessage());
    }
  }
	
}