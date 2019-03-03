package com.example.ynote.ynote;

import java.util.Date;

public class Comments extends CommentsId {

    private String message,userId;
    private Date timestamp;

    public Comments(){
    }

    public Comments(String message, String userId, Date timestamp) {
        this.message = message;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
