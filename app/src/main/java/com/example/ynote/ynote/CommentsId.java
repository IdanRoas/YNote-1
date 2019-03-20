package com.example.ynote.ynote;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class CommentsId {

    @Exclude
    public String CommentsId;

    public <T extends CommentsId> T withId(@NonNull final String id) {
        this.CommentsId = id;
        return (T) this;
    }

}