package com.example.huc_project.profile;

import com.bumptech.glide.RequestManager;
import com.google.firebase.storage.StorageReference;

public class UserRow {
    String user;
    RequestManager glide;
    StorageReference uid;

    public UserRow(String user, StorageReference uid, RequestManager glide) {
        this.user = user;
        this.glide = glide;
        this.uid = uid;
    }

    public String getUid() {
        return String.valueOf(uid);
    }
}