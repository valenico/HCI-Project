package com.example.huc_project.homepage;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.huc_project.profile.User;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Post {
    String title;
    String storageref;
    String postdesc;
    String user;
    Boolean isPackage;
    ArrayList<String> categories;
    String role; //sponsor or sponsorship

    public Post(String title, String storageref, String postdesc, String user, Boolean isPackage, ArrayList<String> categories, String role) {
        this.title = title;
        this.storageref = storageref;
        this.postdesc = postdesc;
        this.user = user;
        this.isPackage = isPackage;
        this.categories = categories;
        this.role = role;
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

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
