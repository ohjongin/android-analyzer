package org.androidanalyzer.gui;

import java.util.Hashtable;

import org.androidanalyzer.R;
import org.androidanalyzer.core.AnalyzerCore;
import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.UICallback;
import org.androidanalyzer.core.utils.Logger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author k.raev
 */
public class AnalyzerStarter extends Activity implements UICallback{
  public static StringBuffer log = new StringBuffer();
  private AnalyzerCore core;
  

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
//    startService(new Intent(this,AnalyzerCore.class));
    core = new AnalyzerCore();
    core.init(this);
    core.setUICallback(this);
    (new Thread(new Updater())).start();
    
    Button button = (Button)findViewById(R.id.AnalyzeButton);
    button.setOnClickListener(mButtonListener);
    

  }

  /**
     * Dummy method to dump info on the screen
     */
  
  class Updater implements Runnable{

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
      boolean looptrue = true;
      while (looptrue) {
        AnalyzerStarter.this.runOnUiThread(new TextUpdater());
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          looptrue = false;
          AnalyzerStarter.this.finish();
        }
      }
    }
    
  }
  
  private OnClickListener mButtonListener = new OnClickListener() {
    
    @Override
    public void onClick(View view) {
      new Thread (new Runnable() {
        
        @Override
        public void run() {
          Data data = core.startAnalysing();
          Logger.log("data:"+data);          
        }
      }).start();    
    }
  };

  
  
  class TextUpdater implements Runnable{

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
      TextView tView = (TextView) AnalyzerStarter.this.findViewById(R.id.test_text_field);
      tView.setText(AnalyzerStarter.log);
    }
    
  }



  /* (non-Javadoc)
   * @see org.androidanalyzer.core.UICallback#updateAnalysisProgress(java.util.Hashtable)
   */
  @Override
  public void updateAnalysisProgress(Hashtable progress) {
    Logger.log(" !!! Progress : "+progress);
    
  }
}