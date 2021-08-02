package com.example.pins.models;

public class MessageModel {
    private String messageId;
    private String message;
    private String timeSent;
    private Long timestamp;
    private String status;

    public static final String STATUS_SENT = "Sent";
    public static final String STATUS_RECEIVED = "Received";

    public MessageModel(){}

    public MessageModel(
            String messageId,
            String message,
            String timeSent,
            Long timestamp,
            String status
    ) {
        this.messageId = messageId;
        this.message = message;
        this.timeSent = timeSent;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
