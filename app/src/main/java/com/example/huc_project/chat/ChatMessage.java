package com.example.huc_project.chat;

import com.bumptech.glide.RequestManager;

import java.util.Date;
import java.util.List;

public class ChatMessage {

    public RequestManager glide;
    private String messageText;
    private String messageUid;
    private List<String> messages;
    private Boolean i_am_0;
    private String document;
    private long messageTime;

    public ChatMessage(RequestManager glide, String messageText, String messageUid, List<String> messages, Boolean i_am_0, String document){
        this.messageText = messageText;
        this.messages = messages;
        this.document = document;
        this.messageUid = messageUid;
        this.glide = glide;
        this.i_am_0 = i_am_0;
        this.messageTime = new Date().getTime();
    }

    public ChatMessage(){
    }

    public String getMessageText(){
        return messageText;
    }

    public List<String> getMessages() {return this.messages; }

    public void setMessages(List<String> messages) {this.messages = messages; }

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

    public Boolean getI_am_0() {
        return i_am_0;
    }

    public void setI_am_0(Boolean i_am_0) {
        this.i_am_0 = i_am_0;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
