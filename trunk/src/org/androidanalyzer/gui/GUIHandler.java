package org.androidanalyzer.gui;

import org.androidanalyzer.Constants;
import org.androidanalyzer.R;
import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.utils.Logger;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author chepilev Define the Handler that receives messages from the thread
 *         and update the progress
 */

public class GUIHandler extends Handler {

	private static final String TAG = "Analyzer-ProgressHandler";

	private ProgressDialog progressDialog;

	private AnalyzerGUI analyzerGUI;
	private boolean setMax = false;

	public GUIHandler(AnalyzerGUI analyzerGUI) {
		this.analyzerGUI = analyzerGUI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Handler#handleMessage(android.os.Message)
	 */
	public void handleMessage(Message msg) {
		Bundle bundle = msg.getData();
		progressDialog = progressDialog == null ? analyzerGUI.getProgressDialog() : progressDialog;
		if (bundle.containsKey(Constants.HANDLER_PROGRESS)) {
			int total = msg.getData().getInt("total");
			int current_plugin = msg.getData().getInt("current");
			Logger.DEBUG(TAG, "[updateAnalysisProgress] current: " + current_plugin);
			String name = msg.getData().getString("name");
			Logger.DEBUG(TAG, "[updateAnalysisProgress] name: " + name);
			if (!setMax) {
				progressDialog.setMax(total);
				setMax = true;
			}
			progressDialog.setProgress(0);
			progressDialog.incrementProgressBy(current_plugin);
			Logger.DEBUG(TAG, "[updateAnalysisProgress] total: " + total);
			progressDialog.setMessage(analyzerGUI.getString(R.string.progress_dialog_msg) + name);
		} else if (bundle.containsKey(Constants.HANDLER_SEND)) {
			analyzerGUI.dismissDialog(Constants.PROGRESS_DIALOG);
			setMax = false;
			analyzerGUI.setLayouts(Constants.LAYOUT_SEND);
			analyzerGUI.createSendButton();
			analyzerGUI.createSaveButton();
			analyzerGUI.setData((Data) bundle.get(Constants.HANDLER_SEND));
		}
	}
}
