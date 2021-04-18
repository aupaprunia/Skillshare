package com.hackathon.skillshare.data;

import java.util.ArrayList;

public class ActivityData {

    String type, description, skill, timestamp, username, mobile, latitude, longitude, image_link, distance, activity_type;
    ArrayList<SubSkillData> sub_skill_data;
    ArrayList<MatchData> match_data;

    public ActivityData(String type, String description, String skill, String timestamp, ArrayList<SubSkillData> sub_skill_data, ArrayList<MatchData> match_data,
                        String username, String mobile, String latitude,String longitude, String image_link, String distance, String activity_type) {
        this.type = type;
        this.description = description;
        this.skill = skill;
        this.timestamp = timestamp;
        this.sub_skill_data = sub_skill_data;
        this.match_data = match_data;
        this.username = username;
        this.mobile = mobile;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image_link = image_link;
        this.distance = distance;
        this.activity_type = activity_type;
    }

    public ActivityData(){}

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<SubSkillData> getSub_skill_data() {
        return sub_skill_data;
    }

    public void setSub_skill_data(ArrayList<SubSkillData> sub_skill_data) {
        this.sub_skill_data = sub_skill_data;
    }

    public ArrayList<MatchData> getMatch_data() {
        return match_data;
    }

    public void setMatch_data(ArrayList<MatchData> match_data) {
        this.match_data = match_data;
    }
}
