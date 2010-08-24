package org.androidanalyzer.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.androidanalyzer.R;
import org.androidanalyzer.core.PluginStatus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class PluginConfiguration extends Activity {

  AnalyzerConfigAdapter adapter;
  ArrayList<PluginStatus> plugins;
  ListView list;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_list);
    list = (ListView)findViewById(R.id.config_list);
    plugins = preparePluginList(getApplicationContext());
    adapter = new AnalyzerConfigAdapter(getApplicationContext(), plugins);
    list.setAdapter(adapter);
    Button saveB = (Button)findViewById(R.id.first_button);
    saveB.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        savePluginState();
      }
    });
    Button resetB = (Button)findViewById(R.id.second_button);
    resetB.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        resetPluginState();
      }
    });
  }
  
  private void resetPluginState() {
    SharedPreferences prefs = getSharedPreferences("org.androidanalyzer.plugin.status", 0);
    Editor edit = prefs.edit();
    String encoded;
    for (PluginStatus toReset : plugins) {
      toReset.setEnabled(true);
      encoded = PluginStatus.encodeStatus(toReset);
      if (encoded != null)
        edit.putString(toReset.getPluginClass(), encoded);
    }
    edit.commit();
    adapter.notifyDataSetChanged();
//    adapter.listItems = plugins;
//    list.setAdapter(adapter);
  }
  
  private void savePluginState() {
    SharedPreferences prefs = getSharedPreferences("org.androidanalyzer.plugin.status", 0);
    PluginStatus saved;
    String record;
    ArrayList<PluginStatus> toSave = new ArrayList<PluginStatus>();
    boolean oldState, newState;
    for (PluginStatus status : plugins) {
      record = prefs.getString(status.getPluginClass(), null);      
      if (record != null) {        
        saved = PluginStatus.decodeStatus(record);
        oldState = saved.isEnabled();
        newState = status.isEnabled();        
        if (oldState != newState) {
          toSave.add(status);
        }
      }
    }
    if (toSave.size() > 0) {
      Editor edit = prefs.edit();
      String encoded;
      for (PluginStatus toSaveStatus : toSave) {
        encoded = PluginStatus.encodeStatus(toSaveStatus);
        if (encoded != null)
          edit.putString(toSaveStatus.getPluginClass(), PluginStatus.encodeStatus(toSaveStatus));
      }
      edit.commit();
    }
    
  }
  
  ArrayList<PluginStatus> preparePluginList(Context ctx) {
    ArrayList<PluginStatus> plugins = new ArrayList<PluginStatus>();
    SharedPreferences prefs = ctx.getSharedPreferences("org.androidanalyzer.plugin.status", 0);
    Map<String, ?> all = prefs.getAll();
    Set<?> values = all.entrySet();
    Iterator<?> it = values.iterator();
    Entry<String, String> record;
    PluginStatus decoded;
    for (;it.hasNext();) {
      record = (Entry<String, String>)it.next();
      decoded = PluginStatus.decodeStatus(record.getValue());
      if (decoded != null)
        plugins.add(decoded);
    }
    return plugins;
  }
  

}
