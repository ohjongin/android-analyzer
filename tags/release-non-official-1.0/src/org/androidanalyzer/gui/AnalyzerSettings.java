package org.androidanalyzer.gui;

import org.androidanalyzer.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class AnalyzerSettings extends TabActivity {

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      int index = 0;
      Resources res = getResources(); // Resource object to get Drawables
      TabHost tabHost = getTabHost();
      TabHost.TabSpec spec;  // Resusable TabSpec for each tab
      Intent intent;  // Reusable Intent for each tab
      // Create an Intent to launch an Activity for the tab (to be reused)
      intent = new Intent().setClass(this, SettingsActivity.class);

      // Initialize a TabSpec for each tab and add it to the TabHost
      spec = tabHost.newTabSpec("core").setIndicator(getString(R.string.tab_core_settings),
                        res.getDrawable(R.drawable.prefs_select))
                    .setContent(intent);
      tabHost.addTab(spec);

      // Do the same for the other tabs
      intent = new Intent().setClass(this, PluginConfiguration.class);
      spec = tabHost.newTabSpec("plugins").setIndicator(getString(R.string.tab_plugin_settings),
                        res.getDrawable(R.drawable.prefs_select))
                    .setContent(intent);
      tabHost.addTab(spec);

      intent = new Intent().setClass(this, AboutActivity.class);
      spec = tabHost.newTabSpec("about").setIndicator(getString(R.string.tab_about),
                        res.getDrawable(R.drawable.about_select))
                    .setContent(intent);
      tabHost.addTab(spec);

      tabHost.setCurrentTab(index);        
  }
  
}
