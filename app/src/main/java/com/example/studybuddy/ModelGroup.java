package com.example.studybuddy;

public class ModelGroup {

    private String uid;
    private String uname;
    private String uemail;
    private String udp;
    private String gname;
    private String description;
    private String gimage;
    private String gtime;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUemail() {
        return uemail;
    }

    public void setUemail(String uemail) {
        this.uemail = uemail;
    }

    public String getUdp() {
        return udp;
    }

    public void setUdp(String udp) {
        this.udp = udp;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGimage() {
        return gimage;
    }

    public void setGimage(String gimage) {
        this.gimage = gimage;
    }

    public String getGtime() {
        return gtime;
    }

    public void setGtime(String gtime) {
        this.gtime = gtime;
    }

    public ModelGroup() {

    }

    public ModelGroup(String uid, String uname, String uemail, String udp, String gname, String description, String gimage, String gtime) {
        this.uid = uid;
        this.uname = uname;
        this.uemail = uemail;
        this.udp = udp;
        this.gname = gname;
        this.description = description;
        this.gimage = gimage;
        this.gtime = gtime;
    }
}
