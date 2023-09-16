package com.example.studybuddy;

public class ModelMeeting
{
    private String datetime, content, gname;

    public ModelMeeting() {

    }

    public ModelMeeting(String datetime, String content, String gname) {
        this.datetime = datetime;
        this.content = content;
        this.gname = gname;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }
}
