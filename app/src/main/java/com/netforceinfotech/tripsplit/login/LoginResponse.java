package com.netforceinfotech.tripsplit.login;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Netforce on 10/15/2016.
 */

public class LoginResponse {

    @SerializedName("api_token")
    private String apiToken;

    private String status;

    @SerializedName("data")
    private List<User> userData;

    @Override
    public String toString() {
        return "LoginResponse{" +
                "apiToken='" + apiToken + '\'' +
                ", status='" + status + '\'' +
                ", userData=" + userData +
                '}';
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<User> getUserData() {
        return userData;
    }

    public void setUserData(List<User> userData) {
        this.userData = userData;
    }
}
