package com.example.huc_project.homepage;

import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.google.firebase.storage.StorageReference;

public class PostHomeRow {
    String title;
    String desc;
    StorageReference img_ref;
    RequestManager glide;

    public PostHomeRow(String title, String desc, StorageReference img, RequestManager glide) {
        this.title = title;
        this.desc = desc;
        this.img_ref = img;
        this.glide = glide;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public StorageReference getImg() {
        return img_ref;
    }

    public void setImg(StorageReference img) {
        this.img_ref = img;
    }

}