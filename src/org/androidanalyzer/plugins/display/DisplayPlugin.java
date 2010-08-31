package org.androidanalyzer.plugins.display;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.androidanalyzer.Constants;
import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.utils.Logger;
import org.androidanalyzer.plugins.AbstractPlugin;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Description
 * 
 * @author <Georgi Chepilev>
 * @version <1.0.0>
 */
public class DisplayPlugin extends AbstractPlugin {
	
	private static final String NAME = "Display Plugin";
  private static final String PLUGIN_VERSION = "1.0.0";
  private static final String PLUGIN_VENDOR = "ProSyst Software GmbH";
	private static final String TAG = "Analyzer-DisplayPlugin";
	private static final String LOCATION = "Location";
	private static final String DISPLAY = "Display";
	private static final String SIZE = "Size";
	private static final String DSPL_SIZE_METRIC = "inch";
	private static final String DSPL_METRIC = "pixels";
	private static final String HRES = "Horizontal Resolution";
	private static final String VRES = "Vertical Resolution";
	private static final String TOUCH = "Touch support";
	private static final String COLOR_DEPTH = "Color depth";
	private static final String COLOR_DEPTH_METRIC = "bits";
	//private static final String DENSITY = "The logical density of the display";
	private static final String DENSITY = "Display density";
	private static final String DENSITY_METRIC = "dpi";
	private static final String REFRESH_RATE = "Refresh rate";
	private static final String REFRESH_RATE_METRIC = "fps";
	private static final String DISPLAY_NAME = "Display-";
	private static final String DESCRIPTION = "Collects data on available displays and their capabilities";
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
	 * @see org.androidanalyzer.plugins.AbstractPlugin#isPluginRequiredUI()
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
	protected Data getData() {
		Logger.DEBUG(TAG, "getData in Display Plugin");
		Data parent = new Data();
		ArrayList<Data> masterChildren = new ArrayList<Data>(2);
		try {
			parent.setName(DISPLAY);
			Display[] displays = getDisplays();
			ArrayList<Data> displayChildren = new ArrayList<Data>(2);
			for (Display display : displays) {
				Data mDisplay = getDisplayMetrics(display, displayChildren);
				Logger.DEBUG(TAG, "mDisplay: " + mDisplay);
				masterChildren.add(mDisplay);
			}
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Display parent node!", e);
			status = "Could not set Display parent node!";
			return null;
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

	private Display[] getDisplays() {
		WindowManager winMgr = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		// TODO How to get all displays connected to the device
		Display[] displays = new Display[1];
		displays[0] = winMgr.getDefaultDisplay();
		Logger.DEBUG(TAG, "displays connected to the device: " + displays[0]);
		return displays;
	}

	private Data getDisplayMetrics(Display display, ArrayList<Data> displayChildren) {
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		Data displayHolder = new Data();
		Data locationHolder = new Data();
		Data sizeHolder = new Data();
		Data xResolutionHolder = new Data();
		Data yResolutionHolder = new Data();
		Data logicalDensityHolder = new Data();
		Data touchHolder = new Data();
		Data colorDepthHolder = new Data();
		Data refreshRateHolder = new Data();

		try {
			locationHolder.setName(LOCATION);
			locationHolder.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_API);
			locationHolder.setValueType(Constants.NODE_VALUE_TYPE_STRING);
			locationHolder.setStatus(Constants.NODE_STATUS_OK);
			displayChildren.add(locationHolder);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Display Location node!", e);
			status = "Could not set Display Location node!";
		}

		try {
			sizeHolder.setName(SIZE);
			double diagonalSizeInInch = Math.sqrt(Math.pow(outMetrics.widthPixels / outMetrics.xdpi, 2)
					+ Math.pow(outMetrics.heightPixels / outMetrics.ydpi, 2));
			Logger.DEBUG(TAG, "Real diagonal size: " + diagonalSizeInInch);
			DecimalFormat twoPlaces = new DecimalFormat("0.0");
			String size = twoPlaces.format(diagonalSizeInInch);
			sizeHolder.setValue(size);
			Logger.DEBUG(TAG, "Display size:" + size);
			sizeHolder.setValueMetric(DSPL_SIZE_METRIC);
			sizeHolder.setValueType(Constants.NODE_VALUE_TYPE_DOUBLE);
			sizeHolder.setStatus(Constants.NODE_STATUS_OK);
			displayChildren.add(sizeHolder);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Display Size node!", e);
			status = "Could not set Display Size node!";
		}

		try {
			xResolutionHolder.setName(HRES);
			xResolutionHolder.setValue(String.valueOf(outMetrics.xdpi));
			Logger.DEBUG(TAG, "H Res:" + outMetrics.xdpi);
			xResolutionHolder.setValueMetric(DSPL_METRIC);
			xResolutionHolder.setValueType(Constants.NODE_VALUE_TYPE_INT);
			xResolutionHolder.setStatus(Constants.NODE_STATUS_OK);
			displayChildren.add(xResolutionHolder);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Display Horizontal Resolution node!", e);
			status = "Could not set Display Horizontal Resolution node!";
		}

		try {
			yResolutionHolder.setName(VRES);
			yResolutionHolder.setValue(String.valueOf(outMetrics.ydpi));
			Logger.DEBUG(TAG, "V Res:" + outMetrics.ydpi);
			yResolutionHolder.setValueMetric(DSPL_METRIC);
			yResolutionHolder.setValueType(Constants.NODE_VALUE_TYPE_INT);
			yResolutionHolder.setStatus(Constants.NODE_STATUS_OK);
			displayChildren.add(yResolutionHolder);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Display Vertical Resolution node!", e);
			status = "Could not set Display Vertical Resolution node!";
		}
		try {
			logicalDensityHolder.setName(DENSITY);
			logicalDensityHolder.setValue(String.valueOf(outMetrics.density));
			Logger.DEBUG(TAG, "Density:" + outMetrics.density);
			logicalDensityHolder.setValueType(Constants.NODE_VALUE_TYPE_INT);
			logicalDensityHolder.setStatus(Constants.NODE_STATUS_OK);
			logicalDensityHolder.setValueMetric(DENSITY_METRIC);
			displayChildren.add(logicalDensityHolder);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Display Denstity node!", e);
			status = "Could not set Display Denstity node!"; 
		}

		try {
			touchHolder.setName(TOUCH);
			touchHolder.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_API);
			touchHolder.setValueType(Constants.NODE_VALUE_TYPE_STRING);
			touchHolder.setStatus(Constants.NODE_STATUS_OK);
			displayChildren.add(touchHolder);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Display Touch Support node!", e);
			status = "Could not set Display Touch Support node!";
		}

		try {
			colorDepthHolder.setName(COLOR_DEPTH);
			colorDepthHolder.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_API);
			colorDepthHolder.setValueType(Constants.NODE_VALUE_TYPE_STRING);
			colorDepthHolder.setValueMetric(COLOR_DEPTH_METRIC);
			colorDepthHolder.setStatus(Constants.NODE_STATUS_OK);
			displayChildren.add(colorDepthHolder);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Display Color Depth node!", e);
			status = "Could not set Display Color Depth node!";
		}

		try {
			refreshRateHolder.setName(REFRESH_RATE);
			refreshRateHolder.setValue(String.valueOf(display.getRefreshRate()));
			Logger.DEBUG(TAG, "Refresh rate:" + display.getRefreshRate());
			refreshRateHolder.setValueType(Constants.NODE_VALUE_TYPE_DOUBLE);
			refreshRateHolder.setValueMetric(REFRESH_RATE_METRIC);
			refreshRateHolder.setStatus(Constants.NODE_STATUS_OK);
			displayChildren.add(refreshRateHolder);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Display Refresh rate node!", e);
			status = "Could not set Display Refresh rate node!";
		}
		/*
		 * TODO: There is no APIs to get values for: Location, Display size, Display
		 * technology, Touch support, Color depth
		 */
		displayHolder = addToParent(displayHolder, displayChildren);
		try {
			displayHolder.setName(DISPLAY_NAME + display.getDisplayId());
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Display node!", e);
			status = "Could not set Display node!";
		}
		return displayHolder;

	}
}
