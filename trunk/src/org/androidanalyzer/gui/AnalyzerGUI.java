package org.androidanalyzer.gui;

import java.net.URL;
import java.util.Hashtable;

import org.androidanalyzer.Constants;
import org.androidanalyzer.R;
import org.androidanalyzer.core.AnalyzerCore;
import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.UICallback;
import org.androidanalyzer.core.utils.Logger;
import org.apache.http.client.HttpResponseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Description
 * 
 * @author <Georgi Chepilev>
 * @version <1.0.0>
 */
public class AnalyzerGUI extends Activity implements UICallback, OnClickListener {
	private static final String TAG = "Analyzer-GUI";

	private static final int MENU_SETTINGS = Menu.FIRST + 1;
	private static final int ALERT_DLG_POSITIVE_SENT = 1;
	private static final int ALERT_DLG_NEGATIVE = 2;
	private static final int ALERT_DLG_POSITIVE_SAVED = 3;

	private Button buttonAnalyze;
	private Button buttonSend;
	private Button buttonSave;
	private EditText mUserText;
	private AlertDialog alert;
	private AlertDialog.Builder builder;
	private AnalyzerCore core;
	public ProgressDialog progressDialog;
	private GUIHandler dataHandler;
	private int current = 0;
	private Data returnData = null;
	private String host = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLayouts(Constants.LAYOUT_ANALYSIS);
		core = new AnalyzerCore();
		core.init(this);
		core.setUICallback(this);
		dataHandler = new GUIHandler(this);
		loadSettings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		core.clearCachedContent();
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view .Menu)
	 */
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(0, MENU_SETTINGS, 0, R.string.menu_settings).setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android. view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SETTINGS:
			Intent intent = new Intent(AnalyzerGUI.this, SettingsGUI.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.core.UICallback#updateAnalysisProgress
	 * (java.util.Hashtable )
	 */
	@Override
	public void updateAnalysisProgress(Hashtable progress) {
		Logger.DEBUG(TAG, "[updateAnalysisProgress] Hashtable: " + progress);
		Logger.DEBUG(TAG, "[updateAnalysisProgress] Plugin started: " + progress.containsKey(UICallback.PLUGIN_STARTED_ANALYZING));
		if (progress.containsKey(UICallback.PLUGIN_STARTED_ANALYZING)) {
			String name = (String) progress.get(UICallback.NAME_OF_PLUGIN);
			int total = (Integer) progress.get(UICallback.NUMBER_OF_PLUGINS);
			Message msg = dataHandler.obtainMessage();
			Bundle bundle = new Bundle();
			bundle.putInt("total", total);
			current++;
			bundle.putInt("current", current);
			bundle.putString("name", name);
			bundle.putString(Constants.HANDLER_PROGRESS, "true");
			msg.setData(bundle);
			dataHandler.sendMessage(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view .View)
	 */
	@Override
	public void onClick(View v) {
		String response = null;
		int typeAlertDlg = 0;
		if (v == buttonSend) {
			// Send button is clicked
			Logger.DEBUG(TAG, "Button 'Send report' is pressed");
			mUserText = (EditText) AnalyzerGUI.this.findViewById(R.id.edittext1);
			String comment = mUserText.getText().toString();
			returnData.setComment(comment);
			Logger.DEBUG(TAG, "User comments: " + comment);
			try {
				URL lHost = new URL(host);
				response = (String) core.sendReport(returnData, lHost);
				typeAlertDlg = ALERT_DLG_POSITIVE_SENT;
			} catch (Exception ex) {
				typeAlertDlg = ALERT_DLG_NEGATIVE;
				if (ex instanceof HttpResponseException) {
					int code = ((HttpResponseException) ex).getStatusCode();
					response = getString(R.string.alert_dialog_warning_msg) + System.getProperty("line.separator") + "Status - " + code;
				} else {
					Logger.ERROR(TAG, "Error is not HttpResponseException ", ex);
				}
			}
			;
			Logger.DEBUG(TAG, "Response: " + response);
			response = response != null ? response : getString(R.string.alert_dialog_warning_msg);
			createAlertDialog(response, typeAlertDlg).show();
			setLayouts(Constants.LAYOUT_ANALYSIS);
		} else if (v == buttonSave) {
			// Save button is clicked
			Logger.DEBUG(TAG, "Button 'Save report' is pressed");
			response = core.writeToFile(returnData);
			typeAlertDlg = ALERT_DLG_POSITIVE_SAVED;
			createAlertDialog(response, typeAlertDlg).show();
			setLayouts(Constants.LAYOUT_ANALYSIS);
		} else if (v == buttonAnalyze) {
			// Analyze button is clicked
			Logger.DEBUG(TAG, "Button 'Analyze' is pressed");
			try {
				showDialog(Constants.PROGRESS_DIALOG);
			} catch (Throwable th) {
				th.printStackTrace();
			}
			new Thread(new AnalyzingProcess(dataHandler, core) {
			}).start();
		}
	}

	/**
	 * Create send button
	 */
	public void createSendButton() {
		buttonSend = (Button) findViewById(R.id.SendButton);
		buttonSend.setOnClickListener(AnalyzerGUI.this);
	}

	/**
	 * Create save button
	 */
	public void createSaveButton() {
		buttonSave = (Button) findViewById(R.id.SaveButton);
		buttonSave.setOnClickListener(AnalyzerGUI.this);
	}

	/**
	 * Switch layouts between entry point layout and send layout
	 */
	public void setLayouts(int layout) {
		switch (layout) {
		case Constants.LAYOUT_ANALYSIS:
			setContentView(R.layout.analisys);
			buttonAnalyze = (Button) findViewById(R.id.AnalyzeButton);
			buttonAnalyze.setOnClickListener(AnalyzerGUI.this);
			return;
		case Constants.LAYOUT_SEND:
			setContentView(R.layout.send);
			current = 0;
			return;
		default:
			return;
		}

	}

	/**
	 * Set data which is returned from analyzing operation via callback
	 * 
	 * @param returnData
	 */
	public void setData(Data returnData) {
		this.returnData = returnData;
	}

	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constants.PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(AnalyzerGUI.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setTitle(getString(R.string.progress_dialog_title));
			progressDialog.setMessage(getString(R.string.progress_dialog_msg));
			// progressDialog.show();
			Logger.DEBUG(TAG, "[onCreateDialog] Progress dialog is created");
			return progressDialog;
		default:
			return null;
		}
	}

	private Dialog createAlertDialog(String msg, int typeDialog) {
		Logger.DEBUG(TAG, "Create alert dialog");
		builder = builder == null ? new AlertDialog.Builder(this) : builder;
		switch (typeDialog) {
		case ALERT_DLG_NEGATIVE:
			builder.setTitle(getString(R.string.alert_dialog_warning_title));
			builder.setIcon(R.drawable.warning_icon_yellow);
			builder.setMessage(msg).setCancelable(false)
					.setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			alert = alert == null ? builder.create() : alert;
			return alert;
		case ALERT_DLG_POSITIVE_SENT:
			builder.setTitle(getString(R.string.alert_dialog_pos_title));
			builder.setIcon(R.drawable.ok_icon);
			builder.setMessage(getString(R.string.alert_dialog_pos_ss) + " " + msg).setCancelable(false)
					.setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			alert = alert == null ? builder.create() : alert;
			return alert;
		case ALERT_DLG_POSITIVE_SAVED:
			builder.setTitle(getString(R.string.alert_dialog_pos_title));
			builder.setIcon(R.drawable.ok_icon);
			builder.setMessage(getString(R.string.alert_dialog_pos_ssd) + " " + getString(R.string.alert_dialog_pos_file_loc) + msg)
					.setCancelable(false).setPositiveButton(getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			alert = alert == null ? builder.create() : alert;
			return alert;
		default:
			return null;
		}
	}

	/**
	 * Loads previously set settings
	 */
	private void loadSettings() {
		host = SettingsGUI.loadSettings(this, Constants.HOST);
		String str_check = SettingsGUI.loadSettings(this, Constants.DEBUG);
		if (str_check != null && str_check.equals("true")) {
			Logger.setDebug(true);
		} else {
			Logger.setDebug(false);
		}

	}

}