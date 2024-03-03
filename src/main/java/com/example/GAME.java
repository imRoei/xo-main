package com.example;

import java.net.Socket;
import com.example.views.*;
import com.example.models.*;

public class GAME {
    private int gridSize;
    private int[][] grid;
    private String player1;
    private String player2;
    private int playersJoined;
    private Socket player1Socket;
    private Socket player2Socket;
    private sockProtocol sock1 = null;
    private sockProtocol sock2 = null;

    private PlayersDB playersDB;
    private PlayerStatsDB playerStatsDB;

    public GAME(int gridSize, String player1, Socket player1Socket) {
        this.gridSize = gridSize;
        this.player1 = player1;
        this.player2 = "Player2";
        this.playersJoined = 1;
        this.setSocket(player1Socket);
        this.grid = new int[gridSize][gridSize];
        try {
            this.playersDB = new PlayersDB();
            this.playerStatsDB = new PlayerStatsDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSocket(Socket socket) {
        if (player1Socket == null) {
            this.player1Socket = socket;
            this.sock1 = new sockProtocol(player1Socket);
        } else {
            this.player2Socket = socket;
            this.sock2 = new sockProtocol(player2Socket);
        }
    }

    public void changeGrid(int x, int y, int value) {
        grid[x][y] = value;
        checkWin();
        // gui1.refreshGrid();
        // gui2.refreshGrid();
        try {
            sock1.send(new GameElements("refreshGrid " + x + " " + y + " " + value));
            sock2.send(new GameElements("refreshGrid " + x + " " + y + " " + value));
            switchGrid(value);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    private void checkWin() {
        int gridSize = this.gridSize;
        int[][] grid = this.grid;
        int value = 0;
        boolean win = false;
        // check rows
        for (int i = 0; i < gridSize; i++) {
            value = grid[i][0];
            if (value == 0) {
                continue;
            }
            win = true;
            for (int j = 1; j < gridSize; j++) {
                if (grid[i][j] != value) {
                    win = false;
                    break;
                }
            }
            if (win) {
                break;
            }
        }
        if (win) {
            win(value);
            return;
        }
        // check columns
        for (int i = 0; i < gridSize; i++) {
            value = grid[0][i];
            if (value == 0) {
                continue;
            }
            win = true;
            for (int j = 1; j < gridSize; j++) {
                if (grid[j][i] != value) {
                    win = false;
                    break;
                }
            }
            if (win) {
                break;
            }
        }
        if (win) {
            win(value);
            return;
        }
        // check diagonals
        value = grid[0][0];
        if (value != 0) {
            win = true;
            for (int i = 1; i < gridSize; i++) {
                if (grid[i][i] != value) {
                    win = false;
                    break;
                }
            }
            if (win) {
                win(value);
                return;
            }
        }
        value = grid[0][gridSize - 1];
        if (value != 0) {
            win = true;
            for (int i = 1; i < gridSize; i++) {
                if (grid[i][gridSize - 1 - i] != value) {
                    win = false;
                    break;
                }
            }
            if (win) {
                win(value);
                return;
            }
        }
        // check draw
        for (int i = 0; i < gridSize; i++)
            for (int j = 0; j < gridSize; j++)
                if (grid[i][j] == 0)
                    return;
        draw();
    }

    public void switchGrid(int value) {
        try {
            if (value == 1) {
                sock1.send(new GameElements("disableGrid"));
                sock2.send(new GameElements("enableGrid"));

            } else {
                sock1.send(new GameElements("enableGrid"));
                sock2.send(new GameElements("disableGrid"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    private void win(int value) {
        try {
            Player player_1 = playersDB.selectByUsername(player1);
            Player player_2 = playersDB.selectByUsername(player2);
            Stats stats1 = playerStatsDB.selectByPlayerId(player_1.getId());
            Stats stats2 = playerStatsDB.selectByPlayerId(player_2.getId());
            if (value == 1) {
                stats1.setWins(stats1.getWins() + 1);
                this.playerStatsDB.update(stats1);
                stats2.setLosses(stats2.getLosses() + 1);
                this.playerStatsDB.update(stats2);
                this.playerStatsDB.saveChanges();
                sock1.send(new GameElements("win"));
                sock2.send(new GameElements("lose"));
            } else {
                stats1.setLosses(stats1.getLosses() + 1);
                this.playerStatsDB.update(stats1);
                stats2.setWins(stats2.getWins() + 1);
                this.playerStatsDB.update(stats2);
                this.playerStatsDB.saveChanges();
                sock1.send(new GameElements("lose"));
                sock2.send(new GameElements("win"));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    private void draw() {
        try {
            Player player_1 = playersDB.selectByUsername(player1);
            Player player_2 = playersDB.selectByUsername(player2);
            Stats stats1 = playerStatsDB.selectByPlayerId(player_1.getId());
            Stats stats2 = playerStatsDB.selectByPlayerId(player_2.getId());
            stats1.setDraws(stats1.getDraws() + 1);
            this.playerStatsDB.update(stats1);
            stats2.setDraws(stats2.getDraws() + 1);
            this.playerStatsDB.update(stats2);
            this.playerStatsDB.saveChanges();
            sock1.send(new GameElements("draw"));
            sock2.send(new GameElements("draw"));

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public int getGrid(int i, int j) {
        return grid[i][j];
    }

    public void updateGUI() {
        try {
            sock1.send(new GameElements("getPlayers " + player1 + " " + player2));
            sock2.send(new GameElements("getPlayers " + player1 + " " + player2));
            sock1.send(new GameElements("isFull true"));
            sock2.send(new GameElements("isFull true"));

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
        this.playersJoined = 2;
        try {
            sock1.send(new GameElements("createGrid"));
            sock2.send(new GameElements("createGrid"));
            updateGUI();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public boolean isFull() {
        return playersJoined == 2;
    }

    public int getGridSize() {
        return gridSize;
    }

    public String[] getPlayers() {
        return new String[] { player1, player2 };
    }

    public void close() {
        if (sock1 != null && !sock1.isClosed()) {
            sock1.send(new GameElements("close"));
            sock1.close();
        }
        if (sock2 != null && !sock2.isClosed()) {
            sock2.send(new GameElements("close"));
            sock2.close();
        }
    }
}
