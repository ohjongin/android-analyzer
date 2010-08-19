package org.androidanalyzer.core;

import java.util.StringTokenizer;

import org.androidanalyzer.R;
import org.androidanalyzer.core.utils.Logger;

public class PluginStatus {
  
  public static final int STATUS_PASSED = R.drawable.ok_icon_3;
  public static final int STATUS_FAILED = R.drawable.failed_icon_3;
  public static final int STATUS_NOT_RUN = R.drawable.not_run_icon_5;
  
  private static final String DELIM = ":";
  
  public static String encodeStatus(PluginStatus pluginStatus) {
    try {
      StringBuffer sb = new StringBuffer(pluginStatus.getPluginClass());
      sb.append(DELIM).append(pluginStatus.getPluginName());
      sb.append(DELIM).append(pluginStatus.getStatus());
      sb.append(DELIM).append(pluginStatus.getLastRun());
      sb.append(DELIM).append(pluginStatus.isEnabled());
      return sb.toString();
    } catch (Throwable t) {
      Logger.ERROR("PluginStatus", t.getMessage());
    }
    return null;
  }
  
  public static final PluginStatus decodeStatus(String pluginStatus) {
    try {
      StringTokenizer sTok = new StringTokenizer(pluginStatus, DELIM, false);
      String pluginClass = sTok.nextToken();
      String pluginName = sTok.nextToken();
      int status = Integer.valueOf(sTok.nextToken());
      long lastRun = Long.valueOf(sTok.nextToken());
      boolean enabled = Boolean.valueOf(sTok.nextToken());
      PluginStatus decoded = new PluginStatus(pluginName, pluginClass, status, lastRun);
      decoded.setEnabled(enabled);
      return decoded;
    } catch (Throwable t) {
      Logger.ERROR("PluginStatus", t.getMessage());
    }
    return null;
  }
  
  private String pluginName;
  private String pluginClass;
  private int status = STATUS_NOT_RUN;
  long lastRun = -1;
  boolean enabled = true;
  
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getPluginName() {
    return pluginName;
  }

  public void setPluginName(String pluginName) {
    this.pluginName = pluginName;
  }

  public String getPluginClass() {
    return pluginClass;
  }

  public void setPluginClass(String pluginClass) {
    this.pluginClass = pluginClass;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public long getLastRun() {
    return lastRun;
  }

  public void setLastRun(long lastRun) {
    this.lastRun = lastRun;
  }

  public PluginStatus(String pluginName, String pluginClass, int status, long lastRun) {
    this.pluginName = pluginName;
    this.pluginClass = pluginClass;
    this.status = status;
    this.lastRun = lastRun;
  }
  

}
