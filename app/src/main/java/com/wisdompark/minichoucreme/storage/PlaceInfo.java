package com.wisdompark.minichoucreme.storage;

import java.io.Serializable;
import java.util.ArrayList;

public class PlaceInfo implements Serializable {
    private String key="";
    private ArrayList<String> macList;
    private ArrayList<String> apList;

    public PlaceInfo(){
        key = "";
        macList = new ArrayList<>();
        apList = new ArrayList<>();
    }

    public PlaceInfo(String dbKey, String key, ArrayList<String> macList, ArrayList<String> apList) {
        this.key = key;
        this.macList = macList;
        this.apList = apList;
    }

    public PlaceInfo(String key, ArrayList<String> macList, ArrayList<String> apList) {
        this.key = key;
        this.macList = macList;
        this.apList = apList;
    }

    @Override
    public String toString() {
        return key+"|"+macList+"|"+apList;
        /*
        return "PlaceInfo{" +
                "key='" + key + '\'' +
                ", macList=" + macList +
                ", apList=" + apList +
                '}';
         */
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<String> getMacList() {
        return macList;
    }

    public void setMacList(ArrayList<String> macList) {
        this.macList = macList;
    }

    public ArrayList<String> getApList() {
        return apList;
    }

    public void setApList(ArrayList<String> apList) {
        this.apList = apList;
    }
}
