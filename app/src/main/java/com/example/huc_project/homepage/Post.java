package com.example.huc_project.homepage;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.huc_project.profile.User;
import com.google.firebase.storage.StorageReference;

public class Post {
    String title;
    String storageref;
    String postdesc;
    String user;
    Boolean isPackage;

    public Post(String title, String storageref, String postdesc, String user, Boolean isPackage) {
        this.title = title;
        this.storageref = storageref;
        this.postdesc = postdesc;
        this.user = user;
        this.isPackage = isPackage;
    }
    public Post() {}




    public String getPostdesc() {
        return postdesc;
    }

    public void setPostdesc(String postdesc) {
        this.postdesc = postdesc;
    }

    public String getStorageref() {
        return storageref;
    }

    public void setStorageref(String storageref) {
        this.storageref = storageref;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() { return user; }
    public Boolean getIsPackage() { return isPackage; }


}
