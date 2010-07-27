package org.androidanalyzer.plugins.dummyplugin;

import java.util.ArrayList;

import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.IAnalyzerPlugin;
import org.androidanalyzer.core.IPluginRegistry;

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
 *
 */
public class DummyExternalPlugin extends Service {

  private static final String NAME = "Dummy plugin External";
  
  private IPluginRegistry pRegistry;
  
  
  /* (non-Javadoc)
   * @see android.app.Service#onCreate()
   */
  @Override
  public void onCreate() {
    super.onCreate();
    boolean conServ = bindService(new Intent(IPluginRegistry.class.getName()),
        regConnection, Context.BIND_AUTO_CREATE);
    System.out.println("service connected :" +conServ);
    System.out.println("Started Dummy Plugin External service !");
  }
  
  /* (non-Javadoc)
   * @see android.app.Service#onBind(android.content.Intent)
   */
  @Override
  public IBinder onBind(Intent intent) {
    return new PluginConnection();
  }
  
  
  /**
   * @return
   */
  private Data returnDummyData() {
    Data dummy = new Data();
    dummy.setName("DummyInfo External");
    Data dummyInfo = new Data();
    dummyInfo.setName("Info about nothing");
    dummyInfo.setValue("no info here");
    dummy.setValue(new ArrayList<Data>().add(dummyInfo));
    return dummy;
  }
  
  
  public class PluginConnection extends IAnalyzerPlugin.Stub {

    /* (non-Javadoc)
     * @see org.androidanalyzer.core.IAnalyzerPlugin#getName()
     */
    @Override
    public String getName() throws RemoteException {
      return NAME;
    }

    /* (non-Javadoc)
     * @see org.androidanalyzer.core.IAnalyzerPlugin#startAnalyze()
     */
    @Override
    public Data startAnalysis() throws RemoteException {
      System.out.println("StartAnalysing in DummyPlugin External");
      return returnDummyData();
    }

    /* (non-Javadoc)
     * @see org.androidanalyzer.core.IAnalyzerPlugin#stopAnalyze()
     */
    @Override
    public void stopAnalysis() throws RemoteException {
      DummyExternalPlugin.stopSelf();
    }

  }
  
  
  public static class BReceiver extends BroadcastReceiver{

    /* (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
      System.out.println("Dummy plugin External broadcast received!");
      context.startService(new Intent(context,
          DummyExternalPlugin.class).putExtra("command", "register"));
      System.out.println("Dummy plugin External tried to start service!");
    }
    
  }
  
  private ServiceConnection regConnection = new ServiceConnection(){

    /* (non-Javadoc)
     * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      System.out.println("OnServConnected Dummy plugin External");
      pRegistry = (IPluginRegistry.Stub.asInterface((IBinder)service));
      try {
        pRegistry.registerPlugin(DummyExternalPlugin.class.getName());
      } catch (RemoteException e) {
        // TODO implement error handling
      }
      unbindService(regConnection);
    }

    /* (non-Javadoc)
     * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
      // TODO Auto-generated method stub
      
    }
    
  };
  
  

}
