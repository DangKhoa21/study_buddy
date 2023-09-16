package com.example.studybuddy;

public class ModelMessage
{
    private String name, message, date, time, uid, uimage, messageID, type;

    public ModelMessage() {

    }

    public ModelMessage(String name, String message, String date, String time, String uid, String uimage, String messageID, String type) {
        this.name = name;
        this.message = message;
        this.date = date;
        this.time = time;
        this.uid = uid;
        this.uimage = uimage;
        this.messageID = messageID;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUimage() {
        return uimage;
    }

    public void setUimage(String uimage) {
        this.uimage = uimage;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
