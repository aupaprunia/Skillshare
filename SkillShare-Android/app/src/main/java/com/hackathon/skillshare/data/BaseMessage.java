package com.hackathon.skillshare.data;

public class BaseMessage {

    String message;
    String username;
    String image_link;
    String contact;
    String timestamp;

    public BaseMessage(String message, String username, String image_link, String contact, String timestamp) {
        this.message = message;
        this.username = username;
        this.image_link = image_link;
        this.contact = contact;
        this.timestamp = timestamp;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BaseMessage(){}

}
