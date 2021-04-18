package com.hackathon.skillshare.data;

import java.util.HashMap;

public class UserData {

    String username, mobile, password, image_link, first_login,latitude,longitude, address;
    HashMap<String,SkillData> skillData;

    public UserData(){}

    public String getFirst_login() {
        return first_login;
    }

    public void setFirst_login(String first_login) {
        this.first_login = first_login;
    }

    public HashMap<String, com.hackathon.skillshare.data.SkillData> getSkillData() {
        return skillData;
    }

    public void setSkillData(HashMap<String, com.hackathon.skillshare.data.SkillData> skillData) {
        this.skillData = skillData;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public UserData(String username, String mobile, String password, String image_link, String first_login, HashMap<String,SkillData> skillData, String latitude, String longitude, String address) {
        this.username = username;
        this.mobile = mobile;
        this.image_link = image_link;
        this.password = password;
        this.first_login = first_login;
        this.skillData = skillData;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
