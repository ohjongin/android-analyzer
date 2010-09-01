package org.androidanalyzer.plugins.api;

import java.util.ArrayList;

import org.androidanalyzer.Constants;
import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.utils.Logger;
import org.androidanalyzer.plugins.AbstractPlugin;

/**
 * APIPlugin class that represents all API sets and version available on the device.
 * 
 */
public class APIPlugin extends AbstractPlugin {

	private static final String NAME = "API Plugin";
	private static final String PLUGIN_VERSION = "1.0.0";
	private static final String PLUGIN_VENDOR = "ProSyst Software GmbH";
	private static final String API = "API";
	private static final String ANDROID_API_LEVEL = "Android API Level";
	private static final String GOOGLE = "Google";
	private static final String GMAPS = "com.google.android.maps";
	private static final String TAG = "Analyzer-APIPlugin";
	private static final String DESCRIPTION = "Collects data on available Android and Google APIs and their versions";
	private String status = Constants.METADATA_PLUGIN_STATUS_PASSED;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.PluginCommunicator#getPluginName()
	 */
	@Override
	public String getPluginName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.PluginCommunicator#getPluginTimeout()
	 */
	@Override
	public long getPluginTimeout() {
		return 10000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.PluginCommunicator#getPluginVersion()
	 */
	@Override
	public String getPluginVersion() {
		return PLUGIN_VERSION;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#getPluginVendor()
	 */
	@Override
	public String getPluginVendor() {
		return PLUGIN_VENDOR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#getPluginDescription()
	 */
	@Override
	public String getPluginDescription() {
		return DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#uiRequired()
	 */
	@Override
	public boolean isPluginRequiredUI() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#getPluginStatus()
	 */
	@Override
	protected String getPluginStatus() {
		return status;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.PluginCommunicator#getData()
	 */
	@Override
	protected Data getData(){
		Logger.DEBUG(TAG, "getData in API Plugin");
		Data parent = new Data();
		ArrayList<Data> masterChildren = new ArrayList<Data>(2);

		try {
			parent.setName(API);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set API parent node!", e);
			status = "Could not set API parent node!";
			return null;
		}
		Data apiLevelHolder = new Data();
		/* API Level */
		try {
			apiLevelHolder.setName(ANDROID_API_LEVEL);
			int apiVersion = getAPIversion();
			if (apiVersion > 0) {
				apiLevelHolder.setValue("" + apiVersion);
				apiLevelHolder.setStatus(Constants.NODE_STATUS_OK);
				apiLevelHolder.setValueType(Constants.NODE_VALUE_TYPE_INT);
			} else {
				apiLevelHolder.setStatus(Constants.NODE_STATUS_FAILED);
				apiLevelHolder.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_VALUE);
				apiLevelHolder.setValueType(Constants.NODE_VALUE_TYPE_STRING);
			}
			apiLevelHolder.setConfirmationLevel(Constants.NODE_CONFIRMATION_LEVEL_TEST_CASE_CONFIRMED);
			apiLevelHolder.setInputSource(Constants.NODE_INPUT_SOURCE_AUTOMATIC);
			masterChildren.add(apiLevelHolder);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set API Level node!", e);
			status = "Could not set API Level node!";
		}

		/* Google */
		Data googleHolder = new Data();
		Data mapViewHolder = new Data();
		try {
			ArrayList<Data> googleChildren = new ArrayList<Data>(2);
			googleHolder.setName(GOOGLE);
			/* Google map */
			mapViewHolder.setName(GMAPS);
			try {
				Object classMapView = Class.forName("com.google.android.maps.MapView").newInstance();
				if (classMapView == null) {
					mapViewHolder.setValue(Constants.NODE_VALUE_NO);
					mapViewHolder.setStatus(Constants.NODE_STATUS_OK);
				}
				mapViewHolder.setValue(Constants.NODE_VALUE_YES);
				mapViewHolder.setStatus(Constants.NODE_STATUS_OK);
			} catch (ClassNotFoundException e) {
				mapViewHolder.setValue(Constants.NODE_VALUE_NO);
				mapViewHolder.setStatus(Constants.NODE_STATUS_OK);
			}
			mapViewHolder.setValueType(Constants.NODE_VALUE_TYPE_BOOLEAN);
			mapViewHolder.setConfirmationLevel(Constants.NODE_CONFIRMATION_LEVEL_TEST_CASE_CONFIRMED);
			mapViewHolder.setInputSource(Constants.NODE_INPUT_SOURCE_AUTOMATIC);
			googleChildren.add(mapViewHolder);

			// TODO Check for others APIs <API Owner Name> <API Package Name>
			googleHolder = addToParent(googleHolder, googleChildren);
			masterChildren.add(googleHolder);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Google node!", e);
			status = "Could not set Google node!";
		}
		parent = addToParent(parent, masterChildren);
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.PluginCommunicator#getPluginClassName()
	 */
	@Override
	protected String getPluginClassName() {
		return this.getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.PluginCommunicator#stopDataCollection()
	 */
	@Override
	protected void stopDataCollection() {
		Logger.DEBUG(TAG, "Service is stopped!");
		this.stopSelf();
	}
}
