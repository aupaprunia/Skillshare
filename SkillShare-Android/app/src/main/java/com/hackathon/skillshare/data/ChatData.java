package com.hackathon.skillshare.data;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatData {
    String username, image_link, message, contact;
    BaseMessage baseMessage;

    public BaseMessage getBaseMessage() {
        return baseMessage;
    }

    public void setBaseMessage(BaseMessage baseMessage) {
        this.baseMessage = baseMessage;
    }

    public ChatData(String username, String image_link, String message, String contact, BaseMessage baseMessage) {
        this.username = username;
        this.image_link = image_link;
        this.message = message;
        this.contact = contact;
        this.baseMessage = baseMessage;
    }


    public ChatData(){}

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
