package com.wisdompark.minichoucreme.storage;

import com.wisdompark.minichoucreme.utils.MiniChouUtils;

public class FPrintInfo {
    String mSenderID;
    long mTime;
    PlaceInfo mPlaceInfo;

    public FPrintInfo() {
        this.mSenderID = "";
        this.mTime = 0;
        this.mPlaceInfo = new PlaceInfo();
    }

    public FPrintInfo(String mSenderID, long mTime, PlaceInfo mPlaceInfo) {
        this.mSenderID = mSenderID;
        this.mTime = mTime;
        this.mPlaceInfo = mPlaceInfo;
    }

    public String getmSenderID() {
        return mSenderID;
    }

    public void setmSenderID(String mSenderID) {
        this.mSenderID = mSenderID;
    }

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public PlaceInfo getmPlaceInfo() {
        return mPlaceInfo;
    }

    public void setmPlaceInfo(PlaceInfo mPlaceInfo) {
        this.mPlaceInfo = mPlaceInfo;
    }

    @Override
    public String toString() {
        return mSenderID
                +"|"+mTime
                +"|"+mPlaceInfo.getKey()
                +"|"+mPlaceInfo.getMacList().get(0)
                +"|"+mPlaceInfo.getApList().get(0);
        /*
        return "FPrintInfo{" +
                "mSenderID='" + mSenderID + '\'' +
                ", mTime=" + mTime +
                ", mPlaceInfo=" + mPlaceInfo +
                '}';

         */
    }
}
