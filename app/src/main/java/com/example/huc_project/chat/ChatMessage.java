package com.example.huc_project.chat;

import com.bumptech.glide.RequestManager;

import java.util.Date;

public class ChatMessage {

    public RequestManager glide;
    private String messageText;
    private String messageUid;
    private long messageTime;

    public ChatMessage(RequestManager glide, String messageText, String messageUid){
        this.messageText = messageText;
        this.messageUid = messageUid;
        this.glide = glide;
        this.messageTime = new Date().getTime();
    }

    public ChatMessage(){
    }

    public String getMessageText(){
        return messageText;
    }

    public void setMessageText(String messageText){
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(String messageUid) {
        this.messageUid = messageUid;
    }
}
