package com.example.huc_project.homepage;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.storage.StorageReference;

class Post {
    String storageref;
    String postdesc;

    public Post(String storageref, String postdesc) {
        this.storageref = storageref;
        this.postdesc = postdesc;
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

}
