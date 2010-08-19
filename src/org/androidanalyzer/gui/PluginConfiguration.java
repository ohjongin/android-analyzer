package org.androidanalyzer.gui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import org.androidanalyzer.R;
import org.androidanalyzer.plugins.AbstractPlugin;
import org.androidanalyzer.plugins.api.APIPlugin;
import org.androidanalyzer.plugins.camera.CameraPlugin;
import org.androidanalyzer.plugins.cpu.CPUPlugin;
import org.androidanalyzer.plugins.display.DisplayPlugin;
import org.androidanalyzer.plugins.location.LocationPlugin;
import org.androidanalyzer.plugins.memory.MemoryPlugin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

public class PluginConfiguration extends Activity {

  AnalyzerConfigAdapter adapter;
  Hashtable<String, Boolean> name2selected;
  Hashtable<String, String> name2description;
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_list);
    name2description = new Hashtable<String, String>();
    name2selected = new Hashtable<String, Boolean>();
    ListView list = (ListView)findViewById(R.id.config_list); 
    adapter = new AnalyzerConfigAdapter(getApplicationContext(), preparePluginList(getApplicationContext(), false), this);
    list.setAdapter(adapter);
  }
  
  ArrayList<AbstractPlugin> preparePluginList(Context ctx, boolean addDefault) {
    ArrayList<AbstractPlugin> plugins = new ArrayList<AbstractPlugin>();
    Calendar cal = Calendar.getInstance();
    cal.set(2010, 7, 1, 16, 12);
    Date time = cal.getTime();
    AbstractPlugin plugin = new APIPlugin();
    name2selected.put(plugin.getPluginName(), Boolean.FALSE);
    name2description.put(plugin.getPluginName(), plugin.getPluginDescription());
    plugins.add(plugin);
    plugin = new CameraPlugin();
    name2selected.put(plugin.getPluginName(), Boolean.TRUE);
    name2description.put(plugin.getPluginName(), plugin.getPluginDescription());
    plugins.add(plugin);
    plugin = new CPUPlugin();
    name2selected.put(plugin.getPluginName(), Boolean.TRUE);
    name2description.put(plugin.getPluginName(), plugin.getPluginDescription());
    plugins.add(plugin);
    plugin = new DisplayPlugin();
    name2selected.put(plugin.getPluginName(), Boolean.FALSE);
    name2description.put(plugin.getPluginName(), plugin.getPluginDescription());
    plugins.add(plugin);
    plugin = new LocationPlugin();
    name2selected.put(plugin.getPluginName(), Boolean.FALSE);
    name2description.put(plugin.getPluginName(), plugin.getPluginDescription());
    plugins.add(plugin);
    plugin = new MemoryPlugin();
    name2selected.put(plugin.getPluginName(), Boolean.TRUE);
    name2description.put(plugin.getPluginName(), plugin.getPluginDescription());
    plugins.add(plugin);
    return plugins;
  }
  

}
