package com.example.pins.models;

public class ContactModel {

    private String userid;
    private String firstname;
    private String lastname;
    private String lastMsg;
    private String lastMsgTime;
    private String imageUrl;
    private Long lastMsgTimestamp;
    private Boolean lastMsgSeen;

    public ContactModel(){}

    public ContactModel(
            String userid,
            String firstname,
            String lastname,
            String imageUrl,
            String lastMsg,
            String lastMsgTime,
            Long lastMsgTimestamp,
            Boolean lastMsgSeen
    ) {
        this.userid = userid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.lastMsg = lastMsg;
        this.lastMsgTime = lastMsgTime;
        this.imageUrl = imageUrl;
        this.lastMsgTimestamp = lastMsgTimestamp;
        this.lastMsgSeen = lastMsgSeen;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getLastMsgTimestamp() {
        return lastMsgTimestamp;
    }

    public void setLastMsgTimestamp(Long lastMsgTimestamp) {
        this.lastMsgTimestamp = lastMsgTimestamp;
    }

    public Boolean getLastMsgSeen() {
        return lastMsgSeen;
    }

    public void setLastMsgSeen(Boolean lastMsgSeen) {
        this.lastMsgSeen = lastMsgSeen;
    }
}
