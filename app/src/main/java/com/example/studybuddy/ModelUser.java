package com.example.studybuddy;

public class ModelUser
{
    public String name , onlineStatus, image, uid ;

    public ModelUser(){

    }

    public ModelUser(String name, String onlineStatus, String image, String uid) {
        this.name = name;
        this.onlineStatus = onlineStatus;
        this.image = image;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
