package com.example.congenialtelegram.Models;

public class UserModel {
    public String uid;
    public boolean isFriend;

    public UserModel(){

    }

    public UserModel(String uid, boolean isFriend){
        this.uid = uid;
        this.isFriend = isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getUid() {
        return uid;
    }

    public boolean getFriend(){
        return isFriend;
    }
}
