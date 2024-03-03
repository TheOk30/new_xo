package com.example.models;

public class Player extends BaseEntity {
    private String username;

    public Player() {
        this.username = "";
    }

    public Player(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}