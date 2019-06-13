package com.example.congenialtelegram.Models;

import java.security.PublicKey;
import java.util.Date;

public class MessageModel {
    public String message;
    public String imageUrl;
    public Date date;
    public Boolean isRead;
    public Boolean isSender;

    public MessageModel(){

    }

    public MessageModel(String message, String imageUrl, Date date){
        this.message = message;
        this.imageUrl = imageUrl;
        this.date = date;
        this.isRead = false;
    }

    public String getMessage() {
        return message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getDate() {
        return date;
    }

    public Boolean getRead() {
        return isRead;
    }

    public Boolean getSender() {
        return isSender;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public void setSender(Boolean sender) {
        isSender = sender;
    }
}
