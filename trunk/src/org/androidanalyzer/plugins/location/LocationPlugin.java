/**
 * 
 */
package org.androidanalyzer.plugins.location;

import java.util.ArrayList;
import java.util.List;

import org.androidanalyzer.Constants;
import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.utils.Logger;
import org.androidanalyzer.plugins.AbstractPlugin;

import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

/**
 * LocatonPlugin class used for gathering Location data
 * 
 * @author k.raev
 */
public class LocationPlugin extends AbstractPlugin {

  private static final String TAG = "Analyzer-LocationPlugin";
  public static final String NAME = "Location Plugin";
  public static final String PARENT_NODE_NAME = "Location";

  private static final String GPS = "GPS";
  private static final String AGPS = "A-GPS support";
  private static final String NETWORK_BASED = "Network based";
  private static final Object NETWORK_PROVIDER = "network";
  private static final Object GPS_PROVIDER = "gps";

  private static final String ASSISTED_GPS_ENABLED = "assisted_gps_enabled";

  /*
   * (non-Javadoc)
   * @see
   * org.androidanalyzer.plugins.location.PluginCommunicator
   * # getData()
   */
  @Override
  public Data getData() {
    ArrayList<Data> children = new ArrayList<Data>(4);
    Data parent = new Data();
    try {
      parent.setName(PARENT_NODE_NAME);
    } catch (Exception e) {
      Logger.DEBUG(TAG, "Could not set Parent node node !");
      Logger.DEBUG(TAG, "Err : " + e.getMessage());
      return null;
    }

    Data gps = null;
    try {
      /* GPS */
      gps = new Data();
      gps.setName(GPS);
      String gpsAvailable = getGpsAvailable();
      if (gpsAvailable != null && gpsAvailable.length() > 0) {
        gps.setValue(gpsAvailable);
        gps.setStatus(Constants.NODE_STATUS_OK);
      } else {
        gps.setStatus(Constants.NODE_STATUS_FAILED);
        gps.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_VALUE);
      }
      children.add(gps);
    } catch (Exception e) {
      Logger.DEBUG(TAG, "Could not set GPS node !");
      Logger.DEBUG(TAG, "Err : " + e.getMessage());
    }

    /* Network Based Positioning */
    try {
      Data netBased = new Data();
      netBased.setName(NETWORK_BASED);
      String netBasedAvailable = getNetworkProviderAvailable();
      if (netBasedAvailable != null && netBasedAvailable.length() > 0) {
        netBased.setValue(netBasedAvailable);
        netBased.setStatus(Constants.NODE_STATUS_OK);
      } else {
        netBased.setStatus(Constants.NODE_STATUS_FAILED);
        netBased.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_VALUE);
      }
      children.add(netBased);
    } catch (Exception e) {
      Logger.DEBUG(TAG, "Could not set Network Based location node");
      Logger.DEBUG(TAG, "Err : " + e.getMessage());
    }

    /* Assisted GPS */
    try {
      if (gps != null) {
        Data agps = new Data();
        agps.setName(AGPS);
        String agpsAvailable = getAgpsAvailable();
        if (agpsAvailable != null && agpsAvailable.length() > 0) {
          agps.setValue(agpsAvailable);
          agps.setStatus(Constants.NODE_STATUS_OK);
        } else {
          agps.setStatus(Constants.NODE_STATUS_FAILED);
          agps.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_VALUE);
        }
        gps.setValue(agps);
      }
    } catch (Exception e) {
      Logger.DEBUG(TAG, "Could not set AGPS node");
      Logger.DEBUG(TAG, "Err : " + e.getMessage());
    }

    addToParent(parent, children);
    return parent;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.androidanalyzer.plugins.location.PluginCommunicator
   * # getPluginName()
   */
  @Override
  public String getPluginName() {
    return NAME;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.androidanalyzer.plugins.PluginCommunicator#getTimeout
   * ()
   */
  @Override
  public long getPluginTimeout() {
    return 10000;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.androidanalyzer.plugins.AbstractPlugin#getPluginVersion
   * ()
   */
  @Override
  public String getPluginVersion() {
    return "1.0.0";
  }

  /*
   * (non-Javadoc)
   * @see org.androidanalyzer.plugins.location.
   * PluginCommunicatorAbstract #getPluginClassName()
   */
  @Override
  protected String getPluginClassName() {
    return this.getClass().getName();
  }

  /*
   * (non-Javadoc)
   * @see org.androidanalyzer.plugins.PluginCommunicator#
   * stopDataCollection()
   */
  @Override
  protected void stopDataCollection() {
    this.stopSelf();
  }

  /**
   * @return
   */
  private String getAgpsAvailable() {
    try {
      if (Settings.Secure.getInt(this.getContentResolver(), ASSISTED_GPS_ENABLED) != 0) {
        return Constants.NODE_VALUE_YES;
      } else {
        return Constants.NODE_VALUE_NO;
      }
    } catch (SettingNotFoundException e) {
      Logger.DEBUG(TAG, "Settings for Agps not found !");
      return null;
    }
  }

  /**
   * @return
   */
  private String getGpsAvailable() {
    LocationManager locMng = getSystemService(Context.LOCATION_SERVICE) != null ? (LocationManager) getSystemService(Context.LOCATION_SERVICE)
        : null;
    if (locMng != null) {
      List<String> providers = locMng.getAllProviders();
      for (String provider : providers) {
        if (provider.equals(GPS_PROVIDER)) {
          Logger.DEBUG(TAG, "Found gps provider : " + provider);
          return Constants.NODE_VALUE_YES;
        }
      }
    }
    return Constants.NODE_VALUE_NO;
  }

  /**
   * @return
   */
  private String getNetworkProviderAvailable() {
    LocationManager locMng = getSystemService(Context.LOCATION_SERVICE) != null ? (LocationManager) getSystemService(Context.LOCATION_SERVICE)
        : null;
    if (locMng != null) {
      List<String> providers = locMng.getAllProviders();
      for (String provider : providers) {
        if (provider.equals(NETWORK_PROVIDER)) {
          Logger.DEBUG(TAG, "Found network provider : " + provider);
          return Constants.NODE_VALUE_YES;
        }
      }
    }
    return Constants.NODE_VALUE_NO;
  }

}
