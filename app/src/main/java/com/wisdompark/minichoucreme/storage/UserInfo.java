package com.wisdompark.minichoucreme.storage;

import java.io.Serializable;
import java.util.ArrayList;

public class UserInfo implements Serializable {
   String uId = "";
   String uToken = "";
   boolean isValid = true;

    public UserInfo() {
    }

    public UserInfo(String uId, String uToken, boolean isValid) {
        this.uId = uId;
        this.uToken = uToken;
        this.isValid = isValid;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuToken() {
        return uToken;
    }

    public void setuToken(String uToken) {
        this.uToken = uToken;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "uId='" + uId + '\'' +
                ", uToken='" + uToken + '\'' +
                ", isValid=" + isValid +
                '}';
    }
}
