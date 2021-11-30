package com.tmvlg.factorcapgame.data.network;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RootModel {

    @JsonProperty("to") //  "to" changed to token
    private String token;

    @JsonProperty("notification")
    private NotificationModel notification;

    @JsonProperty("data")
    private DataModel data;

    @JsonProperty("priority")
    private String priority;

    public RootModel(String token, NotificationModel notification, DataModel data, String priority) {
        this.token = token;
        this.notification = notification;
        this.data = data;
        this.priority = priority;
    }

    @JsonProperty("to") //  "to" changed to token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonProperty("notification")
    public NotificationModel getNotification() {
        return notification;
    }

    public void setNotification(NotificationModel notification) {
        this.notification = notification;
    }

    @JsonProperty("data")
    public DataModel getData() {
        return data;
    }

    public void setData(DataModel data) {
        this.data = data;
    }

    @JsonProperty("priority")
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

}