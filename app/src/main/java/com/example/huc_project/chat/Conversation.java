package com.example.huc_project.chat;

import android.os.Build;
import android.util.Log;

import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class Conversation {

    private String User1;
    private String User2;
    //private String Messages;
    private List<String> Messages;

    public Conversation(){
    }

    public Conversation(String u1, String u2, List<String> messages){
        this.User1 = u1;
        this.User2 = u2;
        this.Messages = messages;
    }


    public String getUser1() {
        return User1;
    }

    public void setUser1(String user1) {
        this.User1 = user1;
    }

    public String getUser2() {
        return User2;
    }

    public void setUser2(String user2) {
        this.User2 = user2;
    }

    public List<String> getMessages() {
        return this.Messages;
    }

    public String getLastMessage(){
        return this.Messages.get(0).substring(1);
    }

    public void setMessages(List<String> messages) {
        this.Messages = messages;
    }
}
