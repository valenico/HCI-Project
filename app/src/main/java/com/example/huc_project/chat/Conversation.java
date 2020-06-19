package com.example.huc_project.chat;

import android.os.Build;
import android.util.Log;

import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class Conversation {

    private String User1;
    private String User2;
    private List<String> Messages;
    private boolean read1;
    private boolean read2;

    public Conversation(){
    }

    public Conversation(String u1, String u2, List<String> messages, boolean read1, boolean reaad2){
        this.User1 = u1;
        this.User2 = u2;
        this.Messages = messages;
        this.read1 = read1;
        this.read2 = read2;
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
        return this.Messages.get(this.Messages.size()-1).substring(1);
    }

    public void setMessages(List<String> messages) {
        this.Messages = messages;
    }

    public boolean isRead1() {
        return read1;
    }

    public void setRead1(boolean read1) {
        this.read1 = read1;
    }

    public boolean isRead2() {
        return read2;
    }

    public void setRead2(boolean read2) {
        this.read2 = read2;
    }
}
