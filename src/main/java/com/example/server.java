package com.example;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class server {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket();
            server.bind(new java.net.InetSocketAddress(sockProtocol.getHost(), sockProtocol.getPort()));
            System.out.println("Server started on port 1234");
            ArrayList<GAME> games = new ArrayList<>();
            while (true) {
                Socket client = server.accept();
                sockProtocol sock = new sockProtocol(client);
                System.out.println("Client connected from " + client.getInetAddress());
                new Thread(() -> {
                    try {
                        GameElements msg = sock.res();
                        String name = msg.getArgs()[0];
                        String size = msg.getArgs()[1];
                        GAME currentGame = null;
                        for (GAME game : games) {
                            if (game.getGridSize() == Integer.parseInt(size) && !game.isFull()) {
                                sock.send(new GameElements("2"));
                                game.setSocket(client);
                                game.setPlayer2(name);
                                currentGame = game;
                                games.remove(game);
                                break;
                            }
                        }
                        if (currentGame == null) {
                            sock.send(new GameElements("1"));
                            currentGame = new GAME(Integer.parseInt(size), name, client);
                        }
                        games.add(currentGame);
                        System.out.println("Game created with size " + size);
                        while (!client.isClosed()) {
                            msg = sock.res();
                            String[] msgArr = msg.getArgs();
                            if (msgArr[0].equals("isFull")) {
                                sock.send(new GameElements("isFull " + String.valueOf(currentGame.isFull())));
                            } else if (msgArr[0].equals("getGridSize")) {
                                sock.send(new GameElements(String.valueOf(currentGame.getGridSize())));
                            } else if (msgArr[0].equals("changeGrid")) {
                                currentGame.changeGrid(Integer.parseInt(msgArr[1]), Integer.parseInt(msgArr[2]),
                                        Integer.parseInt(msgArr[3]));
                            } else if (msgArr[0].equals("getGrid")) {
                                sock.send(new GameElements(String.valueOf(
                                        currentGame.getGrid(Integer.parseInt(msgArr[1]),
                                                Integer.parseInt(msgArr[2])))));
                            } else if (msgArr[0].equals("getPlayers")) {
                                String[] players = currentGame.getPlayers();
                                sock.send(new GameElements("getPlayers " + players[0] + " " + players[1]));
                            } else if (msgArr[0].equals("close")) {
                                currentGame.close();
                                games.remove(currentGame);
                                System.out.println("Client disconnected from " + client.getInetAddress());
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error: ");
                        e.printStackTrace();
                        try {
                            sock.close();
                            client.close();
                            System.out.println("Client disconnected from " + client.getInetAddress());
                        } catch (Exception e2) {
                            System.out.println("Error: ");
                            e2.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            System.out.println("Error: ");
            e.printStackTrace();
        }
    }
}
