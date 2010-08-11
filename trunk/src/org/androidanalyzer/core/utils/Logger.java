package org.androidanalyzer.core.utils;

import android.util.Log;

/**
 * Logger class used to dump messages to the device log
 * 
 * @author <Georgi Chepilev>
 * @version <1.0.0>
 */
public class Logger {
  private static boolean DEBUG = false;
  private static boolean WARNING = true;
  private static boolean ERROR = true;
  private static boolean WRITEDATATOFILE = false;

  /**
   * Dump debug message.
   * 
   * @param tag
   * @param msg
   */
  public static void DEBUG(String tag, String msg) {
    DEBUG(tag, msg, null);
  }

  /**
   * Dump debug message with throwable.
   * 
   * @param tag
   * @param msg
   * @param tr
   */
  public static void DEBUG(String tag, String msg, Throwable tr) {
    if (DEBUG) {
      Log.d("[AAnalyzer DEBUG][ " + tag + " ]", msg, tr);
    }
  }

  /**
   * Dump warning message.
   * 
   * @param tag
   * @param msg
   */
  public static void WARNING(String tag, String msg) {
    WARNING(tag, msg, null);
  }

  /**
   * Dump warning message with throwable.
   * 
   * @param atg
   * @param msg
   * @param tr
   */
  public static void WARNING(String tag, String msg, Throwable tr) {
    if (WARNING) {
      Log.w("[AAnalyzer WARNING][ " + tag + "]", msg, tr);
    }
  }

  /**
   * Dump error message.
   * 
   * @param tag
   * @param msg
   */
  public static void ERROR(String tag, String msg) {
    ERROR(tag, msg, null);
  }

  /**
   * Dump error message with throwable.
   * 
   * @param atg
   * @param msg
   * @param tr
   */
  public static void ERROR(String tag, String msg, Throwable tr) {
    if (ERROR) {
      Log.e("[AAnalyzer ERROR][ " + tag + "]", msg, tr);
    }
  }

  /**
   * Enables/Disables debug
   */
  public static void setDebug(boolean flag) {
    DEBUG = flag;
  }

  /**
   * Returns status of the Debug
   * 
   * @return true if debug enabled ,false if disabled
   */
  public static boolean getDebug() {
    return DEBUG;
  }

  
  /**
   * Enables/Disables writing of the data to a device_data.dat file
   */
  public static void setWriteToFile(boolean flag) {
  	WRITEDATATOFILE = flag;
  }

  /**
   * Returns status of the write to a file
   * 
   * @return true if file writing enabled ,false if disabled
   */
  public static boolean getWriteToFile() {
    return WRITEDATATOFILE;
  }
}
