package com.example.congenialtelegram.Models;

import java.util.Date;

public class CommentModel {
    public String id;
    public String uid;
    public String author;
    public String profileImageUrl;
    public String comment;
    public Date date;

    public CommentModel(){

    }

    public CommentModel(String id, String uid, String comment, Date date){
        this.id = id;
        this.uid = uid;
        this.comment = comment;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getAuthor() {
        return author;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getComment() {
        return comment;
    }

    public Date getDate() {
        return date;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
