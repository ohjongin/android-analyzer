package org.androidanalyzer.gui;

import org.androidanalyzer.Constants;
import org.androidanalyzer.core.Data;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ProgressHandler extends Handler {

  AnalyzerList analyzerList;
  boolean maxSet = false;
  
  public ProgressHandler(AnalyzerList progressActivity) {
    this.analyzerList = progressActivity;
  }

  @Override
  public void handleMessage(Message msg) {
    Bundle bundle = msg.getData();
    if (bundle.containsKey(Constants.HANDLER_PROGRESS)) {
      int total = bundle.getInt("total");
      if (!maxSet) {
        maxSet = true;
      } else {
        total = -1;
      }
      int current_plugin = bundle.getInt("current");
      String name = bundle.getString("name");
      analyzerList.updateProgress(total, current_plugin, name);
    } else if (bundle.containsKey(Constants.HANDLER_SEND)) {
      maxSet = false;
      Data result = (Data)bundle.get(Constants.HANDLER_SEND);
      analyzerList.hideProgress(result);
    }
  }
  
  
}
