package org.androidanalyzer.gui;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.androidanalyzer.Constants;
import org.androidanalyzer.R;
import org.androidanalyzer.core.utils.Logger;
import org.androidanalyzer.transport.Reporter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Description
 * 
 * @author <Georgi Chepilev>
 * @version <1.0.0>
 */
public class SettingsActivity extends Activity {

  private static final String TAG = "Analyzer-Settings-GUI";
  private static final String PREFS_NAME = "org.androidanalyzer.gui.settings";
  private static final int CONNECTION_TEST = 0;

  private CheckBox debug;
  private EditText hostField;
  
  boolean debugEnabled;
  String host;

  /*
   * (non-Javadoc)
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings);
    debug = (CheckBox) findViewById(R.id.Debug);
    hostField = (EditText) findViewById(R.id.Host);
    final Button applyButton = (Button) findViewById(R.id.first_button);
    applyButton.setEnabled(false);
    applyButton.setText(R.string.save_button_txt);
    applyButton.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        boolean d = debug.isChecked();
        if ((d && !debugEnabled) || (!d && debugEnabled)) {
          PreferencesManager.savePreference(SettingsActivity.this, Constants.DEBUG, d);
        }
        String newHost = hostField.getText().toString();
        if (newHost == null || newHost.length() == 0) {
          showAlertDialog();
        } else {
          if (!newHost.equals(host)) {            
            try {
              URI host = new URI(newHost);
              String scheme = host.getScheme();
              if (scheme != null && scheme.startsWith("http://"))
                PreferencesManager.savePreference(SettingsActivity.this, Constants.HOST, newHost);
              else
                showAlertDialog();
            } catch (URISyntaxException e) {
              Logger.ERROR(TAG, "URI is invalid: "+e.getMessage());
              showAlertDialog();
            }
          }
        } 
      }
    });
    host = PreferencesManager.loadStringPreference(this, Constants.HOST);    
    if (host == null) {
      host = Reporter.getHost();
      PreferencesManager.savePreference(this, Constants.HOST, host);
    }
    hostField.setText(host);
    hostField.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        applyButton.setEnabled(true);
      }
      
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }
      
      @Override
      public void afterTextChanged(Editable s) {
      }
    });
    debugEnabled = PreferencesManager.loadBooleanPreference(this, Constants.DEBUG);
    debug.setChecked(debugEnabled);
    debug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if ((isChecked && !debugEnabled) || (!isChecked && debugEnabled))
          applyButton.setEnabled(true);
      }
    });
    Button testLink = (Button)findViewById(R.id.test_link_btn);
    testLink.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        showDialog(CONNECTION_TEST);
        new ProgressThread(pHandler).start();
      }
    });
  }
  
  private void showAlertDialog() {
    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setTitle(getString(R.string.alert_dialog_warning_title));
    alert.setIcon(R.drawable.warning_icon_yellow);
    alert.setMessage(R.string.alert_dialog_warning_host);
    alert.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
      
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    alert.show();
  }
  
  private void showConnectionDialog(boolean success) {
    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setTitle(success ? R.string.alert_dialog_success_title : R.string.alert_dialog_warning_title);
    alert.setIcon(success ? R.drawable.success_icon : R.drawable.warning_icon_yellow);
    alert.setMessage(success ? R.string.alert_connection_ok : R.string.alert_connection_failed);
    alert.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
      
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    alert.show();    
  }
  
   
  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
      case CONNECTION_TEST:
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.alert_connection_test_msg));
        return dialog;
    }
    return null;
  }



  private Handler pHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      Bundle data = msg.getData();
      boolean done = data.getBoolean("done");
      if (done) {
        dismissDialog(CONNECTION_TEST);
        boolean result = data.getBoolean("result");
        showConnectionDialog(result);
      }
    }
    
  };
  
  private class ProgressThread extends Thread {
    
    Handler handler;
    
    public ProgressThread(Handler handler) {
      this.handler = handler;
    }

    @Override
    public void run() {
      Message msg = new Message();
      Bundle data = new Bundle();
      data.putBoolean("done", true);
      boolean result = false;
      try {
        URI test = new URI(host);
        URL connect = test.toURL();
        URLConnection c = connect.openConnection();
        c.setDoOutput(true);
        OutputStream os = c.getOutputStream();
        os.write(1);
        os.close();
        result = true;
      } catch (Throwable t) {
        t.printStackTrace();
      }
      data.putBoolean("result", result);
      msg.setData(data);
      handler.sendMessage(msg);
    }
    
  }
}
