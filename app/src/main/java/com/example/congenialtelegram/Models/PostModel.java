package com.example.congenialtelegram.Models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostModel {
    public String id;
    public String uid;
    public String author;
    public String profileImageUrl;
    public String caption;
    public String imageUrl;
    public Date date;
    public Date lastModifiedDate;
    public int numberOfLikes = 0;
    public int numberOfComments = 0;
    public Map<String, Boolean> likes = new HashMap<>();
    public Map<String, CommentModel> comments = new HashMap<>();

    public PostModel(){

    }

    public PostModel(String id, String uid, String caption, String imageUrl, Date date){
        this.id = id;
        this.uid = uid;
        this.caption = caption;
        this.imageUrl = imageUrl;
        this.date = date;
        this.lastModifiedDate = date;
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

    public String getCaption() {
        return caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getDate() {
        return date;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }
}
