package com.example;

import java.io.Serializable;

public class GameElements implements Serializable {
    private String Topic;
    private String x;
    private String y;
    private String value;
    private String playerNumber;
    private String player1Name;
    private String player2Name;
    private String isfull;
    private String gridSize;
    private String playerName;

    public GameElements(String Topic) {
        this.Topic = Topic;
    }

    public GameElements(String Topic, String str) {
        this.Topic = Topic;

        switch (this.Topic) {
            case "isFull":
                this.isfull = str;
                break;
            case "PlayerNum":
                this.playerNumber = str;
                break;
            case "getGridSize":
                this.gridSize = str;
                break;
            default:
                break;
        }
    }

    public GameElements(String Topic, String str1, String str2) {
        this.Topic = Topic;

        switch (this.Topic) {
            case "getPlayers":
                this.player1Name = str1;
                this.player2Name = str2;
                break;
            case "getGrid":
                this.x = str1;
                this.y = str2;
                break;
            case "start":
                this.playerName = str1;
                this.gridSize = str2;
            default:
                break;
        }
    }

    public GameElements(String Topic, String str1, String str2, String str3) {
        this.Topic = Topic;

        switch (this.Topic) {
            case "changeGrid":
                this.x = str1;
                this.y = str2;
                this.value = str3;
                break;
            case "refreshGrid":
                this.x = str1;
                this.y = str2;
                this.value = str3;
                break;
            default:
                break;
        }
    }

    public String getTopic() {
        return this.Topic;
    }

    public String getX() {
        return this.x;
    }

    public String getY() {
        return this.y;
    }

    public String getValue() {
        return this.value;
    }

    public String getPlayerNumber() {
        return this.playerNumber;
    }

    public String getPlayer1Name() {
        return this.player1Name;
    }

    public String getPlayer2Name() {
        return this.player2Name;
    }

    public String getIsfull() {
        return this.isfull;
    }

    public String getGridSize() {
        return this.gridSize;
    }

    public String getPlayerName() {
        return playerName;
    }
}