package com.hackathon.skillshare.data;

public class OBData {

    String title, des;
    int image;

    public OBData(String title, String des, int image) {
        this.title = title;
        this.des = des;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
