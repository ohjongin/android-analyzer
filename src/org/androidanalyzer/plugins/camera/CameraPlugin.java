/**
 * 
 */
package org.androidanalyzer.plugins.camera;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.androidanalyzer.Constants;
import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.utils.Logger;
import org.androidanalyzer.plugins.AbstractPlugin;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;

/**
 * CameraPlugin class that represents the main CameraPlugin
 * functionality and data gathering
 * 
 * @author k.raev
 */
public class CameraPlugin extends AbstractPlugin {

  private static final String TAG = "Analyzer-CameraPlugin";
  public static final String NAME = "Camera Plugin";

  public static final String PARENT_NODE_NAME = "Camera";
  public static final String PARENT_NODE_NAME_IMAGE = "Image";
  public static final String PARENT_NODE_NAME_VIDEO = "Video";
  public static final String PARENT_NODE_NAME_FORMAT = "Format";
  public static final String PARENT_NODE_NAME_RESOLUTION = "Resolution";
  public static final String PARENT_NODE_NAME_FORMATS = "Formats";
  public static final String PARENT_NODE_NAME_VIDEO_ENCODING = "Video Encoding";
  public static final String PARENT_NODE_NAME_AUDIO_ENCODING = "Audio Encoding";
  public static final String PARENT_NODE_NAME_RESOLUTIONS = "Resolutions";

  private static final String NUMBER_OF_CAMERAS = "Number of cameras";
  private static final String LOCATION = "Location";
  private static final String RESOLUTION = "Resolution";
  private static final String FLASH = "Flash";
  private static final String IMAGE_PARENT_NODE = "Image";
  private static final String IMAGE_FOCUS_MODES = "Focus modes";
  private static final String IMAGE_ZOOM = "Zoom";
  private static final String IMAGE_GEOTAGGING = "Geotagging";
  private static final String IMAGE_HAND_JITTER_REDUCTION = "Hand Jitter Reduction";
  private static final String IMAGE_RED_EYE_REDUCTION = "Red Eye Reduction";
  private static final String IMAGE_FACE_RECOGNITION = "Face Recognition";
  private static final String IMAGE_DYNAMIC_RANGE_OPTIMIZATION = "Dynamic Range Optimization";
  private static final String IMAGE_OTHER_FEATURES = "Other features";
  private static final String IMAGE_FORMAT = "Format";
  private static final String IMAGE_SUPPORTED_FORMATS = "Supported formats";
  private static final String IMAGE_RESOLUTIONS = "Resolutions";
  private static final String IMAGE_RESOLUTIONS_MAXIMUM_RESOLUTION = "Maximum resolution";
  private static final String IMAGE_RESOLUTIONS_SUPPORTED_RESOLUTIONS = "Supported";
  private static final String IMAGE_SUPPORTED_COMPRESSION_RATIOS = "Supported compression ratios";
  private static final String VIDEO_PARENT_NODE = "Video";
  private static final String VIDEO_SELF_TIMER = "Self Timer";
  private static final String VIDEO_FLASH_MOVIE_LIGHT = "Flash / Movie light";
  private static final String ZOOM = "Zoom";
  private static final String VIDEO_WHITE_BALANCE_BRIGHTNESS = "White Balance / Brightness";
  private static final String VIDEO_COLOR_TONE = "Color Tone";
  private static final String VIDEO_SELF_PORTRAIT_VIDEO_MODE = "Self-Portrait Video Mode";
  private static final String VIDEO_LONG_VIDEO_CAPTURE = "Long Video Capture";
  private static final String VIDEO_OTHER_CAPABILITIES = "Other capabilities";
  private static final String VIDEO_FORMATS_MPEG4 = "MPEG-4";
  private static final String VIDEO_FORMATS_3GPP = "3GPP";
  private static final String VIDEO_FORMATS_3GP2 = "3GP2";
  private static final String VIDEO_VIDEO_ENCODING_MPEG4_PART_2 = "MPEG-4 Part 2";
  private static final String VIDEO_VIDEO_ENCODING_MPEG4_PART_10 = "MPEG-4 Part 10";
  private static final String VIDEO_VIDEO_ENCODING_H263 = "H.263";
  private static final String VIDEO_AUDIO_ENCODING_AAC = "AAC";
  private static final String VIDEO_AUDIO_ENCODING_AMR_NB = "AMR-NB";
  private static final String VIDEO_RESOLUTIONS_QCIF_176_X_144 = "QCIF (176 x 144)";
  private static final String VIDEO_RESOLUTIONS_QVGA_320_X_240 = "QVGA (320 x 240)";
  private static final String VIDEO_RESOLUTIONS_CIF_352_X_288 = "CIF (352 x 288)";
  private static final String VIDEO_MAXIMUM_BITRATE = "Maximum bitrate";
  private static final String VIDEO_MAXIMUM_FRAME_RATE = "Maximum frame rate";
  private static final String VIDEO_MAXIMUM_AUDIO_BITRATE = "Maximum audio bitrate";
  private static final String VIDEO_FILE_SIZE_LIMIT = "File size limit";

  private static final String SUPPORTED = "Supported";

  private static final Object[] infoGetters = {
      /* Data Name */
      PARENT_NODE_NAME,
      /* Data direct Node retriving methods */
      "getCameraResolution",
      new Object[] { FLASH, "getFlashSupported", },
      /* Data children nodes */
      /* Image children attributes */
      new Object[] { IMAGE_PARENT_NODE, "getImageZoomSupported", "getImageFocusModes", "getImageRedEyeReduction",
          new Object[] { IMAGE_FORMAT, "getFormatSupportedFormats" },
          new Object[] { IMAGE_RESOLUTIONS, "getResolutionMaximumSupported", "getResolutionSupported" }, },
      /* Video children attributes */
      new Object[] { VIDEO_PARENT_NODE, "getVideoFlashLightSupported", "getVideoSupportedWhiteBalance",
          "getVideoSupportedColorEffects", } };

  private Camera cachedCamera = null;
  private Camera.Parameters[] camParams = null;

  /*
   * (non-Javadoc)
   * @see
   * org.androidanalyzer.plugins.camera.PluginCommunicator#
   * getData()
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

    cachedCamera = Camera.open();
    if (cachedCamera != null) {
      /* Get this properly when api is available */
      int numOfCameras = 1;
      camParams = new Camera.Parameters[numOfCameras];
      for (int i = 0; i < numOfCameras; i++) {
        Data camera = null;
        camera = getCameraInfo(i);
        if (camera != null) {
          children.add(camera);
        }
      }
      if (children != null && children.size() > 0) {
        addToParent(parent, children);
      } else {
        try {
          parent.setStatus(Constants.NODE_STATUS_FAILED);
          parent.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_VALUE);
        } catch (Exception e) {
          Logger.ERROR(TAG, "Could not set values for parent Node !", e);
        }
      }
    } else {
      try {
        parent.setStatus(Constants.NODE_STATUS_FAILED);
        parent.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_VALUE);
      } catch (Exception e) {
        Logger.ERROR(TAG, "Could not set values for parent Node !", e);
      }

    }

    /* Clear cached Camera object */
    if (cachedCamera != null)
      cachedCamera.release();
    cachedCamera = null;
    camParams = null;
    return parent;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.androidanalyzer.plugins.camera.PluginCommunicator#
   * getPluginName()
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
   * @see org.androidanalyzer.plugins.camera.
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
   * @param cameraNumber
   * @return
   */
  private Data getCameraInfo(int cameraNumber) {
    if (infoGetters != null && infoGetters.length > 1) {
      return getInfoThroughGetters(infoGetters, cameraNumber);
    }
    return null;
  }

  /**
   * @param infoGetters
   * @param cameraNumber
   * @return
   */
  private Data getInfoThroughGetters(Object[] infoGetters, int cameraNumber) {
    Data node = null;
    if (infoGetters != null && infoGetters.length > 1) {
      node = new Data();
      if (infoGetters[0] instanceof String && infoGetters[0].equals(PARENT_NODE_NAME)) {
        try {
          node.setName(PARENT_NODE_NAME + "-" + cameraNumber);
        } catch (Exception e) {
          Logger.ERROR(TAG, "Could not set Camera node name !");
          return null;
        }
      } else if (infoGetters[0] instanceof String && ((String) infoGetters[0]).length() > 0) {
        try {
          node.setName((String) infoGetters[0]);
        } catch (Exception e) {
          Logger.ERROR(TAG, "Could not set node name :" + infoGetters[0]);
          return null;
        }
      }

      for (int i = 1; i < infoGetters.length; i++) {
        Object obj = infoGetters[i];
        Data tempData = null;
        if (obj instanceof String) {
          String funcName = (String) obj;
          try {

            Method method = this.getClass().getDeclaredMethod(funcName, int.class);
            Logger.DEBUG(TAG, "Found Method :" + funcName);
            if (method != null)
              try {
                Object result = method.invoke(this, cameraNumber);
                if (result instanceof Data) {
                  tempData = (Data) result;
                }
              } catch (Exception e) {
                Logger.ERROR(TAG, "Error Invoking method : " + method);
                Logger.ERROR(TAG, "ERROR msg : ", e);
              }
          } catch (SecurityException e) {
            Logger.ERROR(TAG, "Could not execute method !");
          } catch (NoSuchMethodException e) {
            Logger.ERROR(TAG, "No such method : " + funcName);
          }
        } else if (obj instanceof Object[]) {
          Object[] subInfoGetters = (Object[]) obj;
          if (subInfoGetters.length > 1) {
            tempData = getInfoThroughGetters(subInfoGetters, cameraNumber);
          }
        } else {
          Logger.ERROR(TAG, "Incorect object in getInfoThroughGetters : " + obj);
        }
        if (tempData != null) {
          try {
            node.setValue(tempData);
          } catch (Exception e) {
            Logger.ERROR(TAG, "Could not set children node to parent node !");
          }
        } else {
          node = dataFailed(node.getName(), Constants.NODE_STATUS_FAILED_UNKNOWN);
          Logger.DEBUG(TAG, "Null tempNode in getInfoThroughGetters for obj : " + obj);
        }
      }
    }
    return node;
  }

  /**
   * @param cameraNumber
   * @return
   */
  private Parameters getCameraParams(int cameraNumber) {
    Camera.Parameters params = camParams[cameraNumber];
    if (params == null) {
      params = cachedCamera.getParameters();
      camParams[cameraNumber] = params;
    }
    return params;
  }

  // ///////////////////////////////////////////////
  /* Getter methods for camera parameters */
  // ///////////////////////////////////////////////
  private Data getAvailableMode(int cameraNumber, String nodeName, String methodName, String fieldName) {
    Camera.Parameters params = getCameraParams(cameraNumber);
    Data data = null;
    Boolean hasFeature = null;
    if (getAPIversion() >= 5) {
      try {
        Method method = Camera.Parameters.class.getDeclaredMethod(methodName);
        if (method != null) {
          List<String> modes = (List<String>) method.invoke(params, null);
          Field auto = Camera.Parameters.class.getDeclaredField(fieldName);
          if (modes != null && auto != null) {
            String fieldValue = (String) auto.get(params);
            if (modes.contains(fieldValue))
              hasFeature = Boolean.TRUE;
            else
              hasFeature = Boolean.FALSE;
          }
        }
      } catch (SecurityException e) {
        Logger.DEBUG(TAG, "Security check failed obtaining method : " + methodName);
      } catch (NoSuchMethodException e) {
        Logger.DEBUG(TAG, "No such method : " + methodName);
      } catch (IllegalArgumentException e) {
      } catch (IllegalAccessException e) {
      } catch (InvocationTargetException e) {
      } catch (NoSuchFieldException e) {
        Logger.DEBUG(TAG, "No such field : " + fieldName);
      }
    }
    if (hasFeature != null) {
      data = new Data();
      try {
        data.setName(nodeName);
        if (hasFeature) {
          data.setValue(Constants.NODE_VALUE_YES);
        } else {
          data.setValue(Constants.NODE_VALUE_NO);
        }
      } catch (Exception e) {
        Logger.ERROR(TAG, "Could not create node name !");
        data = null;
      }
    }
    if (data == null) {
      data = dataFailed(nodeName);
    }
    if (data != null) {
      Logger.DEBUG(TAG, "Node " + nodeName + " value : " + data.getValue());
    }
    return data;
  }

  private Data getAllSupportedModes(int cameraNumber, String nodeName, String methodName) {
    Camera.Parameters params = getCameraParams(cameraNumber);
    Data data = null;
    StringBuffer modeList = null;
    if (getAPIversion() >= 5) {
      try {
        Method method = Camera.Parameters.class.getDeclaredMethod(methodName);
        if (method != null) {
          Object temp = method.invoke(params, null);
          if (temp != null) {
            List<String> modes = (List<String>) temp;
            for (String mode : modes) {
              if (modeList == null) {
                modeList = new StringBuffer();
                modeList.append(mode);
              } else {
                modeList.append("," + mode);
              }
            }
          }
        }
      } catch (SecurityException e) {
        Logger.DEBUG(TAG, "Security check failed obtaining method : " + methodName);
      } catch (NoSuchMethodException e) {
        Logger.DEBUG(TAG, "No such method : " + methodName);
      } catch (IllegalArgumentException e) {
      } catch (IllegalAccessException e) {
      } catch (InvocationTargetException e) {
      }
    }
    if (modeList != null) {
      data = new Data();
      try {
        data.setName(nodeName);
        data.setValue(modeList.toString());
      } catch (Exception e) {
        Logger.ERROR(TAG, "Could not create node : " + nodeName);
        data = null;
      }
    }
    if (data == null && getAPIversion() >= 5) {
      data = dataFailed(nodeName);
    } else if (data == null) {
      data = dataFailed(nodeName, Constants.NODE_STATUS_FAILED_UNAVAILABLE_API);
    }
    if (data != null) {
      Logger.DEBUG(TAG, "Node " + nodeName + " value : " + data.getValue());
    }
    return data;
  }

  private Data getFeatureAvailable(int cameraNumber, String nodeName, String featureCheckupFunc) {
    Camera.Parameters params = getCameraParams(cameraNumber);
    Data data = null;
    Boolean hasFeature = null;
    Method method;
    try {
      if (featureCheckupFunc != null) {
        method = this.getClass().getDeclaredMethod(featureCheckupFunc, int.class);
        if (method != null) {
          Object temp = method.invoke(this, cameraNumber);
          if (temp != null) {
            hasFeature = (Boolean) temp;
          }
        }
      }
    } catch (SecurityException e) {
      Logger.DEBUG(TAG, "Security check failed obtaining method : " + featureCheckupFunc);
    } catch (NoSuchMethodException e) {
      Logger.DEBUG(TAG, "No such method : " + featureCheckupFunc);
    } catch (IllegalArgumentException e) {
    } catch (IllegalAccessException e) {
    } catch (InvocationTargetException e) {
    }

    if (hasFeature != null) {
      data = new Data();
      try {
        data.setName(nodeName);
        if (hasFeature) {
          data.setValue(Constants.NODE_VALUE_YES);
        } else {
          data.setValue(Constants.NODE_VALUE_NO);
        }
      } catch (Exception e) {
        Logger.ERROR(TAG, "Could not create node name !");
        data = null;
      }
    }
    if (data == null && getAPIversion() >= 3) {
      data = dataFailed(nodeName);
    } else if (data == null && getAPIversion() < 3) {
      data = dataFailed(nodeName, Constants.NODE_STATUS_FAILED_UNAVAILABLE_API);
    }
    if (data != null) {
      Logger.DEBUG(TAG, "Node " + nodeName + " value : " + data.getValue());
    }
    return data;
  }

  private Data getFlashSupported(int cameraNumber) {
    String nodeName = SUPPORTED;
    String methodName = "getFlashSupportedInfo";
    return getFeatureAvailable(cameraNumber, nodeName, methodName);
  }

  private Boolean getFlashSupportedInfo(int cameraNumber) {

    String methodName = "getSupportedFlashModes";
    Boolean hasFeature = null;
    if (getAPIversion() >= 5) {
      Camera.Parameters params = getCameraParams(cameraNumber);
      try {
        Method method = Camera.Parameters.class.getDeclaredMethod(methodName);
        if (method != null) {
          Object temp = method.invoke(params, null);
          List<String> modes = null;
          if (temp != null)
            modes = (List<String>) temp;
          if (modes != null && modes.size() > 0)
            hasFeature = Boolean.TRUE;
          else
            hasFeature = Boolean.FALSE;
        }
      } catch (SecurityException e) {
        Logger.DEBUG(TAG, "Security check failed obtaining method : " + methodName);
      } catch (NoSuchMethodException e) {
        Logger.DEBUG(TAG, "No such method : " + methodName);
      } catch (IllegalArgumentException e) {
        Logger.DEBUG(TAG, "IllegalArgumentException :" + e.getMessage());
      } catch (IllegalAccessException e) {
        Logger.DEBUG(TAG, "IllegalAccessException :" + e.getMessage());
      } catch (InvocationTargetException e) {
        Logger.DEBUG(TAG, "InvocationTargetException :" + e.getMessage());
      }
    }
    return hasFeature;
  }

  private Data getImageZoomSupported(int cameraNumber) {
    String nodeName = IMAGE_ZOOM;
    String methodName = "getImageZoomSupportedInfo";
    return getFeatureAvailable(cameraNumber, nodeName, methodName);
  }

  private Boolean getImageZoomSupportedInfo(int cameraNumber) {
    String methodName = "isZoomSupported";

    Camera.Parameters params = getCameraParams(cameraNumber);
    Data data = null;
    Boolean hasFeature = null;
    if (getAPIversion() >= 5) {
      try {
        Method method = Camera.Parameters.class.getDeclaredMethod(methodName);
        if (method != null) {
          Boolean zoomSupported = (Boolean) method.invoke(params, null);
          if (zoomSupported != null) {
            if (zoomSupported)
              hasFeature = Boolean.TRUE;
            else
              hasFeature = Boolean.FALSE;
          }
        }
      } catch (SecurityException e) {
        Logger.DEBUG(TAG, "Security check failed obtaining method : " + methodName);
      } catch (NoSuchMethodException e) {
        Logger.DEBUG(TAG, "No such method : " + methodName);
      } catch (IllegalArgumentException e) {
        Logger.DEBUG(TAG, "IllegalArgumentException :" + e.getMessage());
      } catch (IllegalAccessException e) {
        Logger.DEBUG(TAG, "IllegalAccessException :" + e.getMessage());
      } catch (InvocationTargetException e) {
        Logger.DEBUG(TAG, "InvocationTargetException :" + e.getMessage());
      }
    }
    return hasFeature;
  }

  private Data getVideoSupportedWhiteBalance(int cameraNumber) {
    return getAllSupportedModes(cameraNumber, VIDEO_WHITE_BALANCE_BRIGHTNESS, "getSupportedWhiteBalance");
  }

  private Data getVideoSupportedColorEffects(int cameraNumber) {
    return getAllSupportedModes(cameraNumber, VIDEO_COLOR_TONE, "getSupportedColorEffects");
  }

  private Data getResolutionSupported(int cameraNumber) {
    Camera.Parameters params = getCameraParams(cameraNumber);
    Data data = null;
    StringBuffer sizesStr = null;
    if (getAPIversion() >= 5) {
      String methodName = "getSupportedPictureSizes";
      try {
        Method method = Camera.Parameters.class.getDeclaredMethod(methodName);
        if (method != null) {

          List<Camera.Size> sizes = (List<Size>) method.invoke(params, null);

          if (sizes != null && sizes.size() > 0) {
            for (Size size : sizes) {
              if (sizesStr == null) {
                sizesStr = new StringBuffer();
                sizesStr.append(String.valueOf(size.width) + "x" + size.height);
              } else {
                sizesStr.append("," + size.width + "x" + size.height);
              }
            }
          } else {
            Logger.DEBUG(TAG, "No sizes available!");
          }
        }
      } catch (SecurityException e) {
        Logger.DEBUG(TAG, "Security check failed obtaining method : " + methodName);
      } catch (NoSuchMethodException e) {
        Logger.DEBUG(TAG, "No such method : " + methodName);
      } catch (IllegalArgumentException e) {
      } catch (IllegalAccessException e) {
      } catch (InvocationTargetException e) {
      }
    }

    if (sizesStr != null) {
      data = new Data();
      try {
        data.setName(IMAGE_RESOLUTIONS_SUPPORTED_RESOLUTIONS);
        data.setValue(sizesStr.toString());
      } catch (Exception e) {
        Logger.ERROR(TAG, "Could not create node name !");
        data = null;
      }
    }
    if (data == null && getAPIversion() >= 5) {
      data = dataFailed(IMAGE_RESOLUTIONS_SUPPORTED_RESOLUTIONS);
    } else if (data == null) {
      data = dataFailed(IMAGE_RESOLUTIONS_SUPPORTED_RESOLUTIONS, Constants.NODE_STATUS_FAILED_UNAVAILABLE_API);
    }
    if (data != null) {
      Logger.DEBUG(TAG, "Camera Supported resolutions : " + data.getValue());
    }
    return data;
  }

  private Data getResolutionMaximumSupported(int cameraNumber) {
    Camera.Parameters params = getCameraParams(cameraNumber);
    Data data = null;
    Size size = null;
    if (getAPIversion() >= 5) {
      String methodName = "getSupportedPictureSizes";
      try {
        Method method = Camera.Parameters.class.getDeclaredMethod(methodName);
        if (method != null) {
          List<Camera.Size> sizes = (List<Size>) method.invoke(params, null);
          if (sizes != null && sizes.size() > 0) {
            size = sizes.get(0);
          } else {
            Logger.DEBUG(TAG, "No sizes available!");
          }
        }
      } catch (SecurityException e) {
        Logger.DEBUG(TAG, "Security check failed obtaining method : " + methodName);
      } catch (NoSuchMethodException e) {
        Logger.DEBUG(TAG, "No such method : " + methodName);
      } catch (IllegalArgumentException e) {
      } catch (IllegalAccessException e) {
      } catch (InvocationTargetException e) {
      }
    }
    if ((getAPIversion() > 0 && getAPIversion() < 5) || size == null) {
      size = params.getPictureSize();
    }
    if (size != null) {
      data = new Data();
      try {
        data.setName(IMAGE_RESOLUTIONS_MAXIMUM_RESOLUTION);
        String value = String.valueOf(size.width) + "x" + size.height;
        data.setValue(value);
      } catch (Exception e) {
        Logger.ERROR(TAG, "Could not create node name !");
        data = null;
      }
    }
    if (data == null) {
      data = dataFailed(IMAGE_RESOLUTIONS_MAXIMUM_RESOLUTION);
    }
    if (data != null) {
      Logger.DEBUG(TAG, "Camera Max resolution : " + data.getValue());
    }
    return data;
  }

  private Data getFormatSupportedFormats(int cameraNumber) {
    String nodeName = IMAGE_SUPPORTED_FORMATS;
    String methodName = "getSupportedPictureFormats";
    Camera.Parameters params = getCameraParams(cameraNumber);
    Data data = null;
    StringBuffer formatsSupported = null;
    if (getAPIversion() >= 5) {
      try {
        Method method = Camera.Parameters.class.getDeclaredMethod(methodName);
        if (method != null) {
          List<Integer> formats = (List<Integer>) method.invoke(params, null);
          if (formats != null) {
            for (Integer format : formats) {
              String strFormat = getStrPictureFormat(format);
              if (strFormat != null) {
                if (formatsSupported == null) {
                  formatsSupported = new StringBuffer();
                  formatsSupported.append(strFormat);
                } else {
                  formatsSupported.append("," + strFormat);
                }
              }
            }
          }
        }
      } catch (SecurityException e) {
        Logger.DEBUG(TAG, "Security check failed obtaining method : " + methodName);
      } catch (NoSuchMethodException e) {
        Logger.DEBUG(TAG, "No such method : " + methodName);
      } catch (IllegalArgumentException e) {
      } catch (IllegalAccessException e) {
      } catch (InvocationTargetException e) {
      }
    }
    if (formatsSupported != null && formatsSupported.length() > 0) {
      data = new Data();
      try {
        data.setName(nodeName);
        data.setValue(formatsSupported.toString());
      } catch (Exception e) {
        Logger.ERROR(TAG, "Could not create node : " + nodeName);
        data = null;
      }
    }
    if (data == null && getAPIversion() >= 5) {
      data = dataFailed(nodeName);
    } else if (data == null) {
      data = dataFailed(nodeName, Constants.NODE_STATUS_FAILED_UNAVAILABLE_API);
    }
    if (data != null) {
      Logger.DEBUG(TAG, "Node " + nodeName + " value : " + data.getValue());
    }
    return data;
  }

  private Data getVideoFlashLightSupported(int cameraNumber) {
    return getAvailableMode(cameraNumber, VIDEO_FLASH_MOVIE_LIGHT, "getSupportedFlashModes", "FLASH_MODE_TORCH");
  }

  private Data getImageRedEyeReduction(int cameraNumber) {
    return getAvailableMode(cameraNumber, IMAGE_RED_EYE_REDUCTION, "getSupportedFlashModes", "FLASH_MODE_RED_EYE");
  }

  private Data getImageFocusModes(int cameraNumber) {
    return getAllSupportedModes(cameraNumber, IMAGE_FOCUS_MODES, "getSupportedFocusModes");
  }

  private Data getCameraResolution(int cameraNumber) {
    Camera.Parameters params = getCameraParams(cameraNumber);
    Data data = null;
    Size size = null;
    if (getAPIversion() >= 5) {
      String methodName = "getSupportedPictureSizes";
      try {
        Method method = Camera.Parameters.class.getDeclaredMethod(methodName);
        if (method != null) {
          List<Camera.Size> sizes = (List<Size>) method.invoke(params, null);
          if (sizes != null && sizes.size() > 0) {
            size = sizes.get(0);
          } else {
            Logger.DEBUG(TAG, "No sizes available!");
          }
        }
      } catch (SecurityException e) {
        Logger.DEBUG(TAG, "Security check failed obtaining method : " + methodName);
      } catch (NoSuchMethodException e) {
        Logger.DEBUG(TAG, "No such method : " + methodName);
      } catch (IllegalArgumentException e) {
      } catch (IllegalAccessException e) {
      } catch (InvocationTargetException e) {
      }
    }
    if ((getAPIversion() > 0 && getAPIversion() < 5) || size == null) {
      size = params.getPictureSize();
    }
    if (size != null) {
      data = new Data();
      try {
        data.setName(PARENT_NODE_NAME_RESOLUTION);
        double value = (size.height * size.width);
        value = value / 1000000;
        data.setValue(String.valueOf(value));
        data.setValueMetric("MP");
        data.setValueType(Constants.NODE_VALUE_TYPE_DOUBLE);
      } catch (Exception e) {
        Logger.ERROR(TAG, "Could not create node name !");
        data = null;
      }
    }
    if (data == null) {
      data = dataFailed(PARENT_NODE_NAME_RESOLUTION);
    }
    if (data != null) {
      Logger.DEBUG(TAG, "Camera resolution : " + data.getValue());
    }
    return data;
  }

  /**
   * @param parentNodeNameResolution
   * @return
   */
  private Data dataFailed(String parentNodeName) {
    return dataFailed(parentNodeName, Constants.NODE_STATUS_FAILED_UNAVAILABLE_VALUE);
  }

  /**
   * @param parentNodeNameResolution
   * @return
   */
  private Data dataFailed(String parentNodeName, String reason) {
    Data data = new Data();
    try {
      data.setName(parentNodeName);
      data.setStatus(Constants.NODE_STATUS_FAILED);
      data.setValue(reason);
      data.setValue(Constants.NODE_VALUE_TYPE_STRING);
    } catch (Exception e) {
      Logger.ERROR(TAG, "Could not set failed node : " + parentNodeName);
      data = null;
    }
    return data;
  }

  /**
   * Returns the String reperesentation of a int format
   * value
   * 
   * @param integer
   *          value of the format to be returned as String
   * @return
   */
  private String getStrPictureFormat(Integer format) {
    String formatStr = null;
    switch (format) {
    case -3:
      formatStr = "TRANSLUCENT";
      break;
    case -2:
      formatStr = "TRANSPARENT";
      break;
    case -1:
      formatStr = "OPAQUE";
      break;
    case 1:
      formatStr = "RGBA_8888";
      break;
    case 2:
      formatStr = "RGBX_8888";
      break;
    case 3:
      formatStr = "RGB_888";
      break;
    case 4:
      formatStr = "RGB_565";
      break;
    case 6:
      formatStr = "RGBA_5551";
      break;
    case 8:
      formatStr = "A_8";
      break;
    case 9:
      formatStr = "L_8";
      break;
    case 11:
      formatStr = "RGB_332";
      break;
    case 16:
      formatStr = "NV16";
      break;
    case 17:
      formatStr = "NV21";
      break;
    case 20:
      formatStr = "YUY2";
      break;
    case 256:
      formatStr = "JPEG";
      break;
    default:
      break;
    }
    return formatStr;
  }
}
