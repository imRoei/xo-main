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
    private String PlayerName;

    public String getPlayerName() {
        return PlayerName;
    }

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
                this.PlayerName = str1;
                this.gridSize = str2;
                break;
            default:

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
        return x;
    }

    public String getY() {
        return y;
    }

    public String getValue() {
        return value;
    }

    public String getPlayerNumber() {
        return playerNumber;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public String getIsfull() {
        return isfull;
    }

    public String getGridSize() {
        return gridSize;
    }
}