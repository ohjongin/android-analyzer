package org.androidanalyzer.plugins.dummyplugin;

import java.util.ArrayList;

import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.IAnalyzerPlugin;
import org.androidanalyzer.core.IPluginRegistry;
import org.androidanalyzer.plugins.AbstractPlugin;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * @author k.raev
 */
public class DummyExternalPlugin extends AbstractPlugin {

  private static final String NAME = "Dummy plugin External";

  /**
   * @return
   */
  private Data returnDummyData() {
    Data dummy = new Data();
    try {
      dummy.setName("DummyInfo External");
      Data dummyInfo = new Data();
      dummyInfo.setName("Info about nothing");
      dummyInfo.setValue("no info here");
      dummy.setValue(dummyInfo);
      
    } catch (Exception e) {
      // TODO: handle exception
    }
    return dummy;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.androidanalyzer.plugins.AbstractPlugin#getPluginName
   * ()k
   */
  @Override
  public String getPluginName() {
    return NAME;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.androidanalyzer.plugins.AbstractPlugin#getPluginTimeout
   * ()
   */
  @Override
  public long getPluginTimeout() {
    return 10000;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.androidanalyzer.plugins.AbstractPlugin#getPluginVersion
   * ()
   */
  @Override
  public String getPluginVersion() {

    return "1.0.0";
  }

  /*
   * (non-Javadoc)
   * @see org.androidanalyzer.plugins.AbstractPlugin#
   * getPluginClassName()
   */
  @Override
  protected String getPluginClassName() {
    return this.getClass().getName();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.androidanalyzer.plugins.AbstractPlugin#getData()
   */
  @Override
  protected Data getData() {
    return returnDummyData();
  }

  /*
   * (non-Javadoc)
   * @see org.androidanalyzer.plugins.AbstractPlugin#
   * stopDataCollection()
   */
  @Override
  protected void stopDataCollection() {
    // TODO Auto-generated method stub

  }

}
