
package com.example.pins.ui.message;

import com.google.firebase.Timestamp;

public class message {

    private String message,senderid,timesent;
    boolean sent;
    private Timestamp timestamp;


    public message() {
    }

    public message(String message,  Timestamp timestamp, String timesent, boolean sent) {
        this.message = message;
        this.timestamp = timestamp;
        this.timesent= timesent;
        this.sent= sent;

    }




    public String getTimesent() {
        return timesent;
    }

    public void setTimesent(String timesent) {


        this.timesent = timesent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    }
