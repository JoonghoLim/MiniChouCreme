package com.wisdompark.minichoucreme.utils;

public class Constraints {
    final public static String FPRINT_NAME = "FPRINT";
    final public static String PLACE_NAME = "PLACE";
    final public static String USER_NAME = "USER";
    final public static int RETRY_COUNT = 3;
    final public static String COMMAND_SEND_NAME = "CMD_SEND";
    final public static String COMMAND_RESPONSE_NAME = "CMD_RESP";
    final public static int COMMAND_TYPE_UNKNOWN = 0;
    final public static int COMMAND_TYPE_REMOTESEARCH = 1;
    final public static String INTENT_REMOTE_RESULTS_RECEIVED = "com.wisdompark.minichoucreme.INTENT_REMOTE";
    final public static String INTENT_ALARM_RECEIVED = "com.wisdompark.minichoucreme.INTENT_ALARM_RECEIVED";
    final public static int MNC_STATE_UNKOWN = 0;
    final public static int MNC_STATE_IN_PLACE = 1;
    final public static int MNC_STATE_OUT_OF_PLACE = 2;
    final public static String OUTPLACE_KEY = "OutPlace";
    final public static String OUTPLACE_MAC = "FF:FF:FF:FF:FF:FF";
    final public static String OUTPLACE_APNAME = "OUT_AP";
    final public static int STAYING_INTERVAL_MIN = 30;
    //final public static int STAYING_INTERVAL_MIN = 2; //TEST
    final public static String INTENT_DB_UPDATED = "com.wisdompark.minichoucreme.INTENT_DB_UPDATED";
    final public static String PREF_SENT_KEY = "SENT_KEY";
    final public static String PREF_TIME_KEY = "TIME_KEY";
    final public static String STR_OUT_PLACE = "이전 지역에서 벗어났습니다";
    final public static int NUM_OF_SEARCHED_LIST = 6;
}
