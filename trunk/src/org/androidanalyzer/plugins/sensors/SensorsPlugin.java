package org.androidanalyzer.plugins.sensors;

import java.util.ArrayList;
import java.util.List;

import org.androidanalyzer.Constants;
import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.utils.Logger;
import org.androidanalyzer.plugins.AbstractPlugin;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Description
 * 
 * @author <Georgi Chepilev>
 * @version <1.0.0>
 */
public class SensorsPlugin extends AbstractPlugin {

	private static final String NAME = "Sensors Plugin";
  private static final String PLUGIN_VERSION = "1.0.0";
  private static final String PLUGIN_VENDOR = "ProSyst Software GmbH";
	private static final String TAG = "Analyzer-SensorsPlugin";
	private static final String SENSORS = "Sensors";
	/** Main sensors */
	private static final String ACCELEROMETER = "Accelerometer";
	private static final String PROXIMITY = "Proximity";
	private static final String GYROSCOPE = "Gyroscope";
	private static final String LIGHT = "Light";
	private static final String ORIENTATION = "Orientation";
	private static final String PRESSURE = "Pressure";
	private static final String TEMPERATURE = "Temperature";
	private static final String MAGNETIC = "Magnetic field";
	private static final String SENSOR_NAME = "Name";
	/** Sensors features */
	private static final String SENSORS_CNT = "Sensor-";
	private static final String MAX_RANGE = "Maximum range";
	private static final String POWER = "Power";
	private static final String RESOLUTION = "Resolution";
	private static final String TYPE = "Type";
	private static final String VENDOR = "Vendor";
	private static final String VERSION = "Version";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#getData()
	 */
	@Override
	protected Data getData() {
		Logger.DEBUG(TAG, "getData in Sensor Plugin");
		Data parent = new Data();
		ArrayList<Data> masterChildren = new ArrayList<Data>();
		try {
			parent.setName(SENSORS);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Parent node!", e);
			return null;
		}
		SensorManager sensorMgr = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		masterChildren = getSensorInfo(sensorMgr, masterChildren);
		masterChildren = getSensorExtraInfo(sensorMgr, masterChildren);
		parent = addToParent(parent, masterChildren);
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#getPluginClassName()
	 */
	@Override
	protected String getPluginClassName() {
		return this.getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#getPluginName()
	 */
	@Override
	public String getPluginName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#getPluginTimeout()
	 */
	@Override
	public long getPluginTimeout() {
		return 10000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#getPluginVersion()
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
	 * @see org.androidanalyzer.plugins.AbstractPlugin#stopDataCollection()
	 */
	@Override
	protected void stopDataCollection() {
		Logger.DEBUG(TAG, "Service is stopped!");
		this.stopSelf();
	}

	private ArrayList<Data> getSensorInfo(SensorManager sensorMgr, ArrayList<Data> masterChildren) {
		Data accelerometer = new Data();
		if (!sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER).isEmpty()) {
			try {
				accelerometer.setName(ACCELEROMETER);
				accelerometer.setValue(Constants.NODE_VALUE_YES);
				accelerometer.setValueType(Constants.NODE_VALUE_TYPE_BOOLEAN);
				Logger.DEBUG(TAG, "Accelemoter");
				masterChildren.add(accelerometer);
			} catch (Exception e) {
				Logger.ERROR(TAG, "Failed to set accelerometer sensor", e);
			}
		}

		Data proximity = new Data();
		if (!sensorMgr.getSensorList(Sensor.TYPE_PROXIMITY).isEmpty()) {
			try {
				proximity.setName(PROXIMITY);
				proximity.setValue(Constants.NODE_VALUE_YES);
				proximity.setValueType(Constants.NODE_VALUE_TYPE_BOOLEAN);
				Logger.DEBUG(TAG, "Proximity");
				masterChildren.add(proximity);
			} catch (Exception e) {
				Logger.ERROR(TAG, "Failed to set proximity sensor", e);
			}
		}

		Data giroscope = new Data();
		if (!sensorMgr.getSensorList(Sensor.TYPE_GYROSCOPE).isEmpty()) {
			try {
				giroscope.setName(GYROSCOPE);
				giroscope.setValue(Constants.NODE_VALUE_YES);
				giroscope.setValueType(Constants.NODE_VALUE_TYPE_BOOLEAN);
				Logger.DEBUG(TAG, "Gyroscope");
				masterChildren.add(giroscope);
			} catch (Exception e) {
				Logger.ERROR(TAG, "Failed to set giroscope sensor", e);
			}
		}

		Data light = new Data();
		if (!sensorMgr.getSensorList(Sensor.TYPE_LIGHT).isEmpty()) {
			try {
				light.setName(LIGHT);
				light.setValue(Constants.NODE_VALUE_YES);
				light.setValueType(Constants.NODE_VALUE_TYPE_BOOLEAN);
				Logger.DEBUG(TAG, "Light");
				masterChildren.add(light);
			} catch (Exception e) {
				Logger.ERROR(TAG, "Failed to set light sensor", e);
			}
		}

		Data orientation = new Data();
		if (!sensorMgr.getSensorList(Sensor.TYPE_ORIENTATION).isEmpty()) {
			try {
				orientation.setName(ORIENTATION);
				orientation.setValue(Constants.NODE_VALUE_YES);
				orientation.setValueType(Constants.NODE_VALUE_TYPE_BOOLEAN);
				Logger.DEBUG(TAG, "Orientation");
				masterChildren.add(orientation);
			} catch (Exception e) {
				Logger.ERROR(TAG, "Failed to set orientation sensor", e);
			}
		}

		Data pressure = new Data();
		if (!sensorMgr.getSensorList(Sensor.TYPE_PRESSURE).isEmpty()) {
			try {
				pressure.setName(PRESSURE);
				pressure.setValue(Constants.NODE_VALUE_YES);
				pressure.setValueType(Constants.NODE_VALUE_TYPE_BOOLEAN);
				Logger.DEBUG(TAG, "Pressure");
				masterChildren.add(pressure);
			} catch (Exception e) {
				Logger.ERROR(TAG, "Failed to set pressure sensor", e);
			}
		}
	
		Data temperature = new Data();
		if (!sensorMgr.getSensorList(Sensor.TYPE_TEMPERATURE).isEmpty()) {
			try {
				temperature.setName(TEMPERATURE);
				temperature.setValue(Constants.NODE_VALUE_YES);
				temperature.setValueType(Constants.NODE_VALUE_TYPE_BOOLEAN);
				masterChildren.add(temperature);
				Logger.DEBUG(TAG, "Temperature");
			} catch (Exception e) {
				Logger.ERROR(TAG, "Failed to set temperature sensor", e);
			}
		}

		Data magnetic = new Data();
		if (!sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).isEmpty()) {
			try {
				magnetic.setName(MAGNETIC);
				magnetic.setValue(Constants.NODE_VALUE_YES);
				magnetic.setValueType(Constants.NODE_VALUE_TYPE_BOOLEAN);
				Logger.DEBUG(TAG, "Magnetic");
				masterChildren.add(magnetic);
			} catch (Exception e) {
				Logger.ERROR(TAG, "Failed to set magnetic sensor", e);
			}
		}

		return masterChildren;
	}

	private ArrayList<Data> getSensorExtraInfo(SensorManager sensorMgr, ArrayList<Data> masterChildren) {
		Data parentSensorHolder;
		List<Sensor> allSensors = sensorMgr.getSensorList(Sensor.TYPE_ALL);
		int counter = 0;
		for (Sensor sensor : allSensors) {
			Data sensorHolder = new Data();
			ArrayList<Data> sensorExtraInfoChildren = new ArrayList<Data>();
			try {
				sensorHolder.setName(SENSORS_CNT + counter);

				Data snsName = new Data();
				snsName.setName(SENSOR_NAME);
				String sName = sensor.getName();
				snsName.setValue(sName);
				snsName.setStatus(Constants.NODE_STATUS_OK);
				snsName.setValueType(Constants.NODE_VALUE_TYPE_STRING);
				Logger.DEBUG(TAG, "Sensor name: " + sName);
				sensorExtraInfoChildren.add(snsName);

				Data snsMaxRange = new Data();
				snsMaxRange.setName(MAX_RANGE);
				float maxRange = sensor.getMaximumRange();
				snsMaxRange.setValue(String.valueOf(maxRange));
				snsMaxRange.setValueType(Constants.NODE_VALUE_TYPE_DOUBLE);
				Logger.DEBUG(TAG, "Sensor max range: " + maxRange);
				sensorExtraInfoChildren.add(snsMaxRange);

				Data snsPower = new Data();
				snsPower.setName(POWER);
				float power = sensor.getPower();
				snsPower.setValue(String.valueOf(power));
				snsPower.setValueType(Constants.NODE_VALUE_TYPE_DOUBLE);
				Logger.DEBUG(TAG, "Sensor power: " + power);
				sensorExtraInfoChildren.add(snsPower);

				Data snsResolution = new Data();
				snsResolution.setName(RESOLUTION);
				float resolution = sensor.getResolution();
				snsResolution.setValue(String.valueOf(resolution));
				snsResolution.setValueType(Constants.NODE_VALUE_TYPE_DOUBLE);
				Logger.DEBUG(TAG, "Sensor resolution: " + resolution);
				sensorExtraInfoChildren.add(snsResolution);

				Data snsType = new Data();
				snsType.setName(TYPE);
				int type = sensor.getType();
				snsType.setValue(String.valueOf(type));
				snsType.setValueType(Constants.NODE_VALUE_TYPE_INT);
				Logger.DEBUG(TAG, "Sensor type: " + type);
				sensorExtraInfoChildren.add(snsType);

				Data snsVendor = new Data();
				snsVendor.setName(VENDOR);
				String vendor = sensor.getVendor();
				snsVendor.setValue(vendor);
				snsVendor.setValueType(Constants.NODE_VALUE_TYPE_STRING);
				Logger.DEBUG(TAG, "Sensor vendor: " + vendor);
				sensorExtraInfoChildren.add(snsVendor);

				Data snsVersion = new Data();
				snsVersion.setName(VERSION);
				int version = sensor.getVersion();
				snsVersion.setValue(String.valueOf(version));
				snsVersion.setValueType(Constants.NODE_VALUE_TYPE_INT);
				Logger.DEBUG(TAG, "Sensor version: " + version);
				sensorExtraInfoChildren.add(snsVersion);

				parentSensorHolder = addToParent(sensorHolder, sensorExtraInfoChildren);
				masterChildren.add(parentSensorHolder);
			} catch (Exception e) {
				Logger.ERROR(TAG, "Could not set Sensor node!", e);
			}
			counter++;
		}
		return masterChildren;
	}
}
