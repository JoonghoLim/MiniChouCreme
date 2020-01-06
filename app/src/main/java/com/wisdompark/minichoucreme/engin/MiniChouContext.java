package com.wisdompark.minichoucreme.engin;

import com.wisdompark.minichoucreme.storage.FPrintInfo;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.storage.UserInfo;
import com.wisdompark.minichoucreme.utils.Constraints;

import java.util.ArrayList;

public class MiniChouContext {
    private static MiniChouContext mContext = null;
    private static String mUserID;
    private static ArrayList<PlaceInfo> mPlaceInfoList = new ArrayList<>();
    private static ArrayList<FPrintInfo> mFPrintInfoList = new ArrayList<>();
    private static ArrayList<UserInfo> mUserInfoList = new ArrayList<>();
    private static PlaceInfo mCurrentPlaceInfo = new PlaceInfo();
    private static PlaceInfo mLastSentPlaceInfo = new PlaceInfo();
    private static PlaceInfo mRealTimeSearchInfo = new PlaceInfo();
    private static int mCurrentState = Constraints.MNC_STATE_UNKOWN;
    private static int mLastState = Constraints.MNC_STATE_UNKOWN;
    private static boolean isParentsMode = false;
    private static String watching_email = "";
    private static String myEmail = "";
    private static long firstLeavingTime = 0;

    public static ArrayList<UserInfo> getmUserInfoList() {
        return mUserInfoList;
    }

    public static void setmUserInfoList(ArrayList<UserInfo> mUserInfoList) {
        MiniChouContext.mUserInfoList = mUserInfoList;
    }

    public static long getFirstLeavingTime(){
        return firstLeavingTime;
    }

    public static void setFirstLeavingTime(long leavingTime){
        firstLeavingTime = leavingTime;
    }

    public static boolean isIsParentsMode() {
        return isParentsMode;
    }

    public static void setIsParentsMode(boolean isParentsMode) {
        MiniChouContext.isParentsMode = isParentsMode;
    }

    public static String getWatching_email() {
        String strTmp = watching_email;
        if(MiniChouContext.isIsParentsMode() != true) //자식모드 일때
            strTmp = MiniChouContext.getMyEmail();

        strTmp = strTmp.replace(".","_").trim();
        if(strTmp.lastIndexOf("@") >= 0)
            strTmp = strTmp.substring(0,strTmp.lastIndexOf("@")).trim();

        return strTmp;
        //return "TEST_STR2";
        //return "TEST_STR";
    }

    public static void setWatching_email(String watching_email) {
        MiniChouContext.watching_email = watching_email;
    }

    public static String getMyEmail() {
        return myEmail;
        //return "TEST_STR2@TEST.com";
        //return "TEST_STR@TEST.com";
    }

    public static void setMyEmail(String myEmail) {
        MiniChouContext.myEmail = myEmail;
    }

    public static ArrayList<FPrintInfo> getmFPrintInfoList() {
        return mFPrintInfoList;
    }

    public static void setmFPrintInfoList(ArrayList<FPrintInfo> mFPrintInfoList) {
        MiniChouContext.mFPrintInfoList = mFPrintInfoList;
    }

    public static int getmCurrentState() {
        return mCurrentState;
    }

    public static void setmCurrentState(int mCurrentState) {
        MiniChouContext.mCurrentState = mCurrentState;
    }

    public static int getmLastState() {
        return mLastState;
    }

    public static void setmLastState(int mLastState) {
        MiniChouContext.mLastState = mLastState;
    }



    public static PlaceInfo getmRealTimeSearchInfo() {
        boolean isTest = false;

        if(isTest)
            mRealTimeSearchInfo = getTestInfo(); //Test Version

        return mRealTimeSearchInfo;
    }

    private static PlaceInfo getTestInfo(){
        long currentMills = System.currentTimeMillis();
        String key = "Key"+currentMills;
        ArrayList<String> macList = new ArrayList<>();
        ArrayList<String> apList = new ArrayList<>();

        for(int i=0 ;i<3;i++) {
            macList.add(i+"-"+currentMills);
            apList.add(i+"-"+currentMills);
        }

        return new PlaceInfo(key,macList,apList);
    }

    public static void setmRealTimeSearchInfo(PlaceInfo mRealTimeSearchInfo) {
        MiniChouContext.mRealTimeSearchInfo = mRealTimeSearchInfo;
    }

    private MiniChouContext(){
    }

    public static synchronized  MiniChouContext getInstance(){
        if(mContext == null)
            mContext = new MiniChouContext();

        return mContext;
    }

    public static String getmUserID() {
        if(getMyEmail().length() == 0)
            return mUserID;
        else
            return getMyEmail();
    }

    public static void setmUserID(String mUserID) {
        MiniChouContext.mUserID = mUserID;
    }

    public static ArrayList<PlaceInfo> getmPlaceInfoList() {
        return mPlaceInfoList;
    }

    public static void setmPlaceInfoList(ArrayList<PlaceInfo> mPlaceInfoList) {
        MiniChouContext.mPlaceInfoList = mPlaceInfoList;
    }

    public static PlaceInfo getmCurrentPlaceInfo() {
        return mCurrentPlaceInfo;
    }

    public static void setmCurrentPlaceInfo(PlaceInfo mCurrentPlaceInfo) {
        MiniChouContext.mCurrentPlaceInfo = mCurrentPlaceInfo;
    }

    public static PlaceInfo getmLastSentPlaceInfo() {
        return mLastSentPlaceInfo;
    }

    public static void setmLastSentPlaceInfo(PlaceInfo mLastSentPlaceInfo) {
        MiniChouContext.mLastSentPlaceInfo = mLastSentPlaceInfo;
    }
}
