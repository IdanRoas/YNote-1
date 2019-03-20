package com.example.ynote.ynote;

import java.util.Date;

public class Comments extends CommentsId {

    private String message, userId, noteId,noteTitle;
    private Date timestamp;

    public Comments(){

    }

    public Comments(String message, String userId, String noteId,String noteTitle, Date timestamp) {
        this.message = message;
        this.userId = userId;
        this.noteId = noteId;
        this.noteTitle = noteTitle;
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

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}