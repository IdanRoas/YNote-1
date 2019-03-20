package com.example.ynote.ynote;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class NotificationsId {

    @Exclude
    public String NotificationsId;

    public <T extends NotificationsId> T withId(@NonNull final String id) {
        this.NotificationsId = id;
        return (T) this;
    }
}
