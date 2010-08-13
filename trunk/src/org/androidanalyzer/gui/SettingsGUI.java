package org.androidanalyzer.gui;

import java.net.URI;

import org.androidanalyzer.Constants;
import org.androidanalyzer.R;
import org.androidanalyzer.core.utils.Logger;
import org.androidanalyzer.transport.Reporter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Description
 * 
 * @author <Georgi Chepilev>
 * @version <1.0.0>
 */
public class SettingsGUI extends Activity implements OnClickListener {

  private static final String TAG = "Analyzer-Settings-GUI";
  private static final String PREFS_NAME = "org.androidanalyzer.gui.SettingsGUI";

  private CheckBox debug;
  private EditText hostEditText;
  private Button applyButton;
  private AlertDialog alert;
  private AlertDialog.Builder builder;

  /*
   * (non-Javadoc)
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings);
    debug = (CheckBox) findViewById(R.id.Debug);
    hostEditText = (EditText) findViewById(R.id.Host);
    String lHost = loadSettings(this, Constants.HOST);
    if (lHost == null) {
      String dHost = Reporter.getHost();
      hostEditText.setText(dHost);
      saveSettings(this, Constants.HOST, dHost);
    } else {
      hostEditText.setText(lHost);
    }
    String str_check = loadSettings(this, Constants.DEBUG);
    if (str_check != null && str_check.equals("true")) {
      debug.setChecked(true);
      Logger.setDebug(true);
    } else {
      debug.setChecked(false);
      Logger.setDebug(false);
    }
    applyButton = (Button) findViewById(R.id.Apply);
    applyButton.setOnClickListener(this);
    debug.setOnClickListener(this);
  }

  /*
   * (non-Javadoc)
   * @see
   * android.view.View.OnClickListener#onClick(android.view
   * .View)
   */
  public void onClick(View v) {
    if (v == debug) {
      if (((CheckBox) v).isChecked()) {
        Logger.DEBUG(TAG, "Debug is enabled");
        Logger.setDebug(true);
        saveSettings(this, Constants.DEBUG, "true");
      } else {
        Logger.DEBUG(TAG, "Debug is disabled");
        Logger.setDebug(false);
        saveSettings(this, Constants.DEBUG, "false");
      }
    }
    if (v == applyButton) {
      String mHost = null;
      mHost = hostEditText.getText().toString();
      if (mHost == null || mHost.length() == 0) {
        createAlertDialog().show();
      } else {
        try {
          Logger.DEBUG(TAG, "Host is " + mHost);
          new URI(mHost);
        } catch (Exception e) {
          Logger.WARNING(TAG, "URI is invalid", e);
          createAlertDialog().show();
        }
        saveSettings(this, Constants.HOST, mHost);
        Reporter.setHost(hostEditText.getText().toString());
      }
    }
  }

  public static void saveSettings(Activity activity, String key, String value) {
    SharedPreferences.Editor prefs = activity.getSharedPreferences(PREFS_NAME, 0).edit();
    Logger.DEBUG(TAG, "Store key:value - " + key + ":" + value);
    prefs.putString(key, value);
    prefs.commit();
  }

  public static String loadSettings(Activity activity, String key) {
    SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, 0);
    String value = prefs.getString(key, null);
    Logger.DEBUG(TAG, "Load key:value - " + key + ":" + value);
    return value;
  }

  private Dialog createAlertDialog() {
    Logger.DEBUG(TAG, "Host URI is invalid");
    builder = builder == null ? new AlertDialog.Builder(this) : builder;
    builder.setTitle(getString(R.string.alert_dialog_warning_title));
    builder.setIcon(R.drawable.warning_icon_yellow);
    builder.setMessage(R.string.alert_dialog_warning_host).setCancelable(false)
        .setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
          }
        });
    alert = alert == null ? builder.create() : alert;
    return alert;
  }
}
