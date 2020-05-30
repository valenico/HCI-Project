package com.example.huc_project.profile;

import com.bumptech.glide.RequestManager;

public class SocialRow {
    Social social;
    RequestManager glide;


    public SocialRow(Social social, RequestManager glide) {
        this.social = social;
        this.glide = glide;
    }
}
