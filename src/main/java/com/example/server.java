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
                        GameElements elements = sock.res();
                        String Topic = elements.getTopic();
                        GAME currentGame = null;

                        if (elements.getTopic().equals("start")) {
                            String playerName = elements.getPlayerName();
                            String size = elements.getGridSize();

                            for (GAME game : games) {
                                if (game.getGridSize() == Integer.parseInt(size) && !game.isFull()) {
                                    sock.send(new GameElements("PlayerNum", "2"));
                                    game.setSocket(client);
                                    game.setPlayer2(playerName);
                                    currentGame = game;
                                    games.remove(game);
                                    break;
                                }
                            }
                            if (currentGame == null) {
                                sock.send(new GameElements("PlayerNum", "1"));
                                currentGame = new GAME(Integer.parseInt(size), playerName, client);
                            }
                            games.add(currentGame);
                            System.out.println("Game created with size " + size);
                        }

                        else if (Topic.equals("isFull")) {
                            sock.send(new GameElements("isFull ", String.valueOf(currentGame.isFull())));
                        } else if (Topic.equals("getGridSize")) {
                            sock.send(new GameElements(String.valueOf(currentGame.getGridSize())));
                        } else if (Topic.equals("changeGrid")) {
                            currentGame.changeGrid(Integer.parseInt(elements.getX()),
                                    Integer.parseInt(elements.getY()),
                                    Integer.parseInt(elements.getValue()));
                        } 

                        else if (Topic.equals("getGrid")) {
                            sock.send(new GameElements(String.valueOf(
                            currentGame.getGrid(Integer.parseInt(elements.getX()),
                            Integer.parseInt(elements.getY())))));
                        }
                        
                          
                        else if (Topic.equals("getPlayers")) {
                            String[] players = currentGame.getPlayers();
                            sock.send(new GameElements("getPlayers", players[0], players[1]));
                        } else if (Topic.equals("close")) {
                            currentGame.close();
                            games.remove(currentGame);
                            System.out.println("Client disconnected from " + client.getInetAddress());
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
