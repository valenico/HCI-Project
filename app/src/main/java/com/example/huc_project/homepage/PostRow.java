package com.example.huc_project.homepage;

import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.RequestManager;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class PostRow {
    Post post;
    StorageReference img_ref;
    RequestManager glide;

    public PostRow(Post post, StorageReference img, RequestManager glide) {
        this.post = post;
        this.img_ref = img;
        this.glide = glide;
    }

    public Post getPost(){ return post;}

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostRow postRow = (PostRow) o;
        return Objects.equals(post, postRow.post) &&
                Objects.equals(img_ref, postRow.img_ref) &&
                Objects.equals(glide, postRow.glide);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(post, img_ref, glide);
    }
}