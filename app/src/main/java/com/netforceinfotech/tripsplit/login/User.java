package com.netforceinfotech.tripsplit.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Netforce on 10/15/2016.
 */

public class User {

    @SerializedName("email_id")
    private String emailId;

    private String msg;

    @SerializedName("user_id")
    private String userId;

    @Override
    public String toString() {
        return "User{" +
                "emailId='" + emailId + '\'' +
                ", msg='" + msg + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
