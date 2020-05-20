package com.example.huc_project.profile;

import com.example.huc_project.homepage.Post;
import com.example.huc_project.homepage.PostRow;

public class User {
    String name;
    String country;
    String city;
    String mail;
    String phone;
    Boolean hidden_mail;
    String description;
    String[] interests;
    Post[] posts;
    Post[] bm_posts;
    User[] bm_users;




    public User(String name, String country, String city, String mail, String phone, Boolean hidden_mail, String description, String[] interests, Post[] posts, Post[] bm_posts, User[] bm_users) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.mail = mail;
        this.phone = phone;
        this.hidden_mail = hidden_mail;
        this.description = description;
        this.interests = interests;
        this.posts = posts;
        this.bm_posts = bm_posts;
        this.bm_users = bm_users;
    }

    public User() {}

    public String getName() { return name; }
    public String getCountry() { return  country; }
    public String getCity() { return city; }
    public String getMail() { return mail; }
    public String getPhone() { return phone; }
    public Boolean getHidden_mail() { return hidden_mail; }
    public String getDescription() { return description; }
    public String[] getInterests() { return interests; }
    public Post[] getPosts() { return posts; }
    public Post[] getBm_posts() { return bm_posts; }
    public User[] getBm_users() { return bm_users; }

    public void setName(String name) { this.name = name; }
    public void setCountry(String country) { this.country = country; }
    public void setCity(String city) { this.country = city; }
    public void setMail(String mail) { this.mail = mail; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setHidden_mail(Boolean hidden_mail) { this.hidden_mail =  hidden_mail; }
    public void setDescription(String description) { this.description = description; }
    public void setInterests(String[] interests) { this.interests = interests; }
}
