package com.jpcn.chatapp.Model;

public class ChatUser {
    public String id;

    public ChatUser(String id) {
        this.id = id;
    }

    public ChatUser() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
