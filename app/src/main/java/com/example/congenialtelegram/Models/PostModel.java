package com.example.congenialtelegram.Models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostModel {
    public String id;
    public String author;
    public String caption;
    public String imageUrl;
    public Date date;
    public int numberOfLikes = 0;
    public int numberOfComments = 0;
    public int numberOfShares = 0;
    public Map<String, Boolean> likes = new HashMap<>();
    public Map<String, String> comments = new HashMap<>();
    public Map<String, Boolean> shares = new HashMap<>();

    public PostModel(){

    }

    public PostModel(String id, String author, String caption, String imageUrl, Date date){
        this.id = id;
        this.author = author;
        this.caption = caption;
        this.imageUrl = imageUrl;
        this.date = date;
    }

    public String getAuthor() {
        return author;
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

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public int getNumberOfShares() {
        return numberOfShares;
    }
}
