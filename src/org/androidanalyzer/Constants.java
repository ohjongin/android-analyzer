package org.androidanalyzer;

/**
 * Class containing constants used in the Analyzer
 */
public class Constants {
  public static final String PLUGINS_DISCOVERY_INTENT = "org.androidanalyzer.plugins.discovery";

  public static final String NODE_NAME = "analyzer.node.name";
  public static final String NODE_VALUE = "analyzer.node.value";
  public static final String NODE_STATUS = "analyzer.node.status";
  public static final String NODE_VALUE_TYPE = "analyzer.node.value.type";
  public static final String NODE_VALUE_YES = "Y";
  public static final String NODE_VALUE_NO = "N";
  public static final String NODE_VALUE_METRIC = "analyzer.node.value.metric";
  public static final String NODE_COMMENT = "analyzer.node.comment";
  public static final String NODE_CONFIRMATION_LEVEL = "analyzer.node.confirmation.level";
  public static final String NODE_CONFIRMATION_LEVEL_TEST_CASE_CONFIRMED = "TCC";
  public static final String NODE_CONFIRMATION_LEVEL_USER_CONFIRMED = "UC";
  public static final String NODE_CONFIRMATION_LEVEL_UNCONFIRMED = "U";
  public static final String NODE_INPUT_SOURCE = "analyzer.node.input.source";
  public static final String NODE_INPUT_SOURCE_AUTOMATIC = "A";
  public static final String NODE_INPUT_SOURCE_MANUAL = "M";

  public static final String NODE_VALUE_TYPE_STRING = "ValueTypeString";
  public static final String NODE_VALUE_TYPE_INT = "ValueTypeInteger";
  public static final String NODE_VALUE_TYPE_LONG = "ValueTypeLong";
  public static final String NODE_VALUE_TYPE_DOUBLE = "ValueTypeDouble";
  public static final String NODE_VALUE_TYPE_BOOLEAN = "ValueTypeBoolean";
  public static final String NODE_VALUE_TYPE_DATA = "ValueTypeData";

  public static final String NODE_STATUS_OK = "OK";
  public static final String NODE_STATUS_FAILED = "FAILED";
  public static final String NODE_STATUS_FAILED_UNAVAILABLE_API = "FAILED_UNAVAILABLE_API";
  public static final String NODE_STATUS_FAILED_UNAVAILABLE_VALUE = "FAILED_UNAVAILABLE_VALUE";
  public static final String NODE_STATUS_FAILED_UNKNOWN = "FAILED_UNKNOWN";

  public static final String VALUE_NULL = "Value is null!";
  public static final String VALUE_TYPE_INCORRECT = "Value type is incorrect!";
  public static final String NO_VALUE_IN_DATA_OBJECT = "Data Object has no value!";

  public static final String HANDLER_PROGRESS = "handler.progress";
  public static final String HANDLER_SEND = "handler.send";

  public static final int PROGRESS_DIALOG = 0;

  public static final int LAYOUT_ANALYSIS = 1;
  public static final int LAYOUT_SEND = 2;
  public static final String ROOT_DATA = "Data";
  public static final String MD5_IMEI = "md5.imei";

}
