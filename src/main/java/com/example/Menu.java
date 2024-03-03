package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;

import com.example.views.PlayersDB;
import com.example.views.PlayerStatsDB;

import com.example.models.*;

import java.net.Socket;
import java.util.ArrayList;
import java.io.IOException;

public class Menu {

    private PlayersDB playersDB;
    private PlayerStatsDB playerStatsDB;

    @FXML
    private TextField gridSize;
    @FXML
    private TextField playerName;
    @FXML
    private Label error;

    @FXML
    public void Init() {
        try {
            this.playerStatsDB = new PlayerStatsDB();
            this.playersDB = new PlayersDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                int gridSize = Integer.parseInt(this.gridSize.getText());
                Platform.runLater(() -> {
                    try {
                        processGridSize(gridSize);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (NumberFormatException e) {
                Platform.runLater(() -> error.setText("Error: Grid size must be an integer"));
            }
        }).start();
    }

    private void processGridSize(int gridSize) throws IOException {
        try {
            if (gridSize < 3) {
                error.setText("Error: Grid size must be at least 3");
            } else if (gridSize > 7) {
                error.setText("Error: Grid size must be at most 7");
            } else {
                GUI gui = new GUI(1, gridSize);
                Socket gameSocket = new Socket(sockProtocol.getHost(), sockProtocol.getPort());
                sockProtocol sock = new sockProtocol(gameSocket);
                gui.setGame(gameSocket, sock);
                sock.send(new GameElements("start", this.playerName.getText(), this.gridSize.getText()));
                try {
                    Player player = new Player(playerName.getText());
                    ArrayList<BaseEntity> players = playersDB.selectAll();
                    boolean exists = false;
                    for (BaseEntity p : players) {
                        if (((Player) p).getUsername().equals(playerName.getText())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        playersDB.insert(player);
                        playersDB.saveChanges();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("GameWindow.fxml"));
                loader.setController(gui);
                App.getPrimaryStage().setScene(new Scene(loader.load(), 640, 480));
                App.getPrimaryStage().setOnCloseRequest(e -> {
                    sock.send(new GameElements("close"));
                });
                new Thread(() -> {
                    Player player = playersDB.selectByUsername(playerName.getText());
                    Stats stats = playerStatsDB.selectByPlayerId(player.getId());

                    while (true) {
                        GameElements elements = sock.res();
                        String Topic = elements.getTopic();

                        Platform.runLater(() -> {
                            if (Topic.equals("PlayerNum")) {
                                gui.setValue(Integer.parseInt(elements.getPlayerNumber()));
                            }

                            else if (Topic.equals("refreshGrid")) {
                                gui.updateGrid(Integer.parseInt(elements.getX()), Integer.parseInt(elements.getY()),
                                        Integer.parseInt(elements.getValue()));
                            }

                            else if (Topic.equals("enableGrid")) {
                                gui.enableGrid();
                            }

                            else if (Topic.equals("disableGrid")) {
                                gui.disableGrid();
                            }

                            else if (Topic.equals("win")) {
                                try {
                                    gui.win(stats, playerName.getText());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            else if (Topic.equals("lose")) {
                                try {
                                    gui.lose(stats, playerName.getText());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            else if (Topic.equals("draw")) {
                                try {
                                    gui.draw(stats, playerName.getText());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            else if (Topic.equals("refresh")) {
                                gui.refresh();
                            }

                            else if (Topic.equals("createGrid")) {
                                gui.createGrid();
                            }

                            else if (Topic.equals("getPlayers")) {
                                gui.setPlayer1(elements.getPlayer1Name());
                                gui.setPlayer2(elements.getPlayer2Name());
                                gui.updatePlayerNames();
                            }

                            else if (Topic.equals("isFull")) {
                                gui.setIsFull(elements.getIsfull());
                                gui.playersJoined();
                            }

                            else if (Topic.equals("close")) {
                                sock.close();
                                try {
                                    this.playerStatsDB.close();
                                    this.playersDB.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
