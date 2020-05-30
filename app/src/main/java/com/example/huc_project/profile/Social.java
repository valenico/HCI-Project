package com.example.huc_project.profile;

public class Social {
    String name;
    String identity;
    String user;
    String followers;
    String lw;
    String lm;

    public Social(String name, String identity, String user, String followers, String lw, String lm) {
        this.name = name;
        this.identity = identity;
        this.user = user;
        this.followers = followers;
        this.lm = lm;
        this.lw = lw;
    }

    public Social() {
    }
}