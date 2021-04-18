package com.hackathon.skillshare.data;

import java.util.ArrayList;

public class ListData {

    String username, mobile, image_link, description, skill, latitude, longitude, timestamp, distance;
    ArrayList<SubSkillData> sub_skill_data;

    public ListData(){}

    public ListData(String username, String mobile, String image_link, String description, String skill, String latitude, String longitude, String timestamp, String distance, ArrayList<SubSkillData> sub_skill_data) {
        this.username = username;
        this.mobile = mobile;
        this.image_link = image_link;
        this.description = description;
        this.skill = skill;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.distance = distance;
        this.sub_skill_data = sub_skill_data;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public ArrayList<SubSkillData> getSub_skill_data() {
        return sub_skill_data;
    }

    public void setSub_skill_data(ArrayList<SubSkillData> sub_skill_data) {
        this.sub_skill_data = sub_skill_data;
    }
}
