package com.example;

import java.io.Serializable;

public class GameElements implements Serializable {
    private String IP;
    private String[] args;

    public GameElements(String message, String IP) {
        String[] parts = message.split(" ");
        this.IP = IP;
        if (parts.length > 0) {
            args = message.split(" ");
        } else {
            args = new String[0];
        }
    }

    public GameElements(String message) {
        String[] parts = message.split(" ");
        if (parts.length > 0) {
            args = message.split(" ");
        } else {
            args = new String[0];
        }
    }

    public String[] getArgs() {
        return args;
    }

    public String getIP() {
        return IP;
    }

    public String getArg(int index) {
        if (index >= 0 && index < args.length) {
            return args[index];
        }
        return null; // Or throw an exception depending on how you want to handle errors
    }
}
