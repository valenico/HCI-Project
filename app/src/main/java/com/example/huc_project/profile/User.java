package com.example.huc_project.profile;

import com.example.huc_project.homepage.Post;
import com.example.huc_project.homepage.PostRow;

import java.util.ArrayList;

public class User {
    String name;

    public User(String name) {
        this.name = name;
    }

    public User() {}

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
