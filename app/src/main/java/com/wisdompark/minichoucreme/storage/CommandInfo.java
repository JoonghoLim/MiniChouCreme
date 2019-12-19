package com.wisdompark.minichoucreme.storage;

public class CommandInfo {
    private String mSenderId;
    private int mCMDType;
    private int mCMDNum;
    private long mTime;
    private PlaceInfo obj;

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public CommandInfo() {
        this.mSenderId = "";
        this.mCMDType = 0;
        this.mCMDNum = 0;
        this.obj = null;
    }

    public CommandInfo(String mSenderId, int mCMDType, int mCMDNum, long time, PlaceInfo obj) {
        this.mSenderId = mSenderId;
        this.mCMDType = mCMDType;
        this.mCMDNum = mCMDNum;
        this.mTime = time;
        this.obj = obj;
    }

    public String getmSenderId() {
        return mSenderId;
    }

    public void setmSenderId(String mSenderId) {
        this.mSenderId = mSenderId;
    }

    public int getmCMDType() {
        return mCMDType;
    }

    public void setmCMDType(int mCMDType) {
        this.mCMDType = mCMDType;
    }

    public int getmCMDNum() {
        return mCMDNum;
    }

    public void setmCMDNum(int mCMDNum) {
        this.mCMDNum = mCMDNum;
    }

    public PlaceInfo getObj() {
        return obj;
    }

    public void setObj(PlaceInfo obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return "CommandInfo{" +
                "mSenderId='" + mSenderId + '\'' +
                ", mCMDType=" + mCMDType +
                ", mCMDNum=" + mCMDNum +
                ", mTime=" + mTime +
                ", obj=" + obj +
                '}';
    }
}
