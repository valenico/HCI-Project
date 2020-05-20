package com.example.huc_project.homepage;

import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.google.firebase.storage.StorageReference;

public class PostRow {
    Post post;
    StorageReference img_ref;
    RequestManager glide;

    public PostRow(Post post, StorageReference img, RequestManager glide) {
        this.post = post;
        this.img_ref = img;
        this.glide = glide;
    }

    public String getTitle() {
        return post.title;
    }

    public void setTitle(String title) {
        this.post.title = title;
    }

    public String getDesc() {
        return post.postdesc;
    }

    public void setDesc(String desc) {
        this.post.postdesc = desc;
    }

    public StorageReference getImg() {
        return img_ref;
    }

    public void setImg(StorageReference img) {
        this.img_ref = img;
    }

}