package com.example.huc_project.chat;

import java.util.ArrayList;

public class Conversation {

    private String User1;
    private String User2;
    private ArrayList<String> Messages;

    public Conversation(){
    }

    public Conversation(String u1, String u2, ArrayList<String> messages){
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

    public ArrayList<String> getMessages() {
        return this.Messages;
    }

    public String getLastMessage(){
        return this.Messages.get(this.Messages.size()-1);
    }

    public void setMessages(ArrayList<String> messages) {
        this.Messages = messages;
    }
}
