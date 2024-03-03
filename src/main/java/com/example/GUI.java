package com.example;

import java.sql.ResultSet;

import com.example.models.Stats;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;

import java.net.Socket;

public class GUI implements Initializable, gameInterface {
    private SocketHandler sock;
    private int value;
    private final int gridSize;
    private String isFull;
    private int[][] grid;
    private String player1, player2;

    @FXML
    private Label playerName;

    @FXML
    private Label player2Name;

    @FXML
    private Label centerLabel;

    @FXML
    private Label playerScore;

    @FXML
    private Label wins;

    @FXML
    private Label draws;

    @FXML
    private Label losses;

    @FXML
    private GridPane gridPane;

    public GUI(int value, int gridSize) {
        this.value = value;
        this.gridSize = gridSize;
        this.grid = new int[gridSize][gridSize];
    }

    void setIsFull(String isFull) {
        this.isFull = isFull;
    }

    void setPlayer1(String player1) {
        this.player1 = player1;
    }

    void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public void setGame(SocketHandler sock) {
        this.sock = sock;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    public void updateGrid(int i, int j, int value) {
        this.grid[i][j] = value;
        refreshGrid();
    }

    public void refresh() {
        sock.Send(new GameElements("getPlayers"));
        sock.Send(new GameElements("isFull"));
    }

    public void createGrid() {
        try {
            if (isFull == "false") {
                return;
            }
            gridPane.getChildren().clear();
            for (int i = 0; i < gridSize; i++) {
                gridPane.addRow(i);
                for (int j = 0; j < gridSize; j++) {
                    gridPane.addColumn(j);
                    Button button = new Button();
                    button.setOnAction((ActionEvent event) -> {
                        int x = GridPane.getColumnIndex(button);
                        int y = GridPane.getRowIndex(button);
                        try {
                            sock.Send(new GameElements("changeGrid",String.valueOf(x),String.valueOf(y),String.valueOf(value)));
                        } catch (Exception e) {
                            System.out.println("Error: " + e);
                        }
                    });
                    button.setStyle("-fx-font-size: 40px; -fx-min-width: 120px; -fx-min-height: 120px;");
                    if (value == 2) {
                        button.setDisable(true);
                    }
                    gridPane.add(button, i, j);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void disableGrid() {
        try {
            for (int i = 0; i < gridSize; i++) {
                gridPane.addRow(i);
                for (int j = 0; j < gridSize; j++) {
                    gridPane.addColumn(j);
                    Button button = (Button) gridPane.getChildren().get(i * gridSize + j);
                    button.setDisable(true);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void enableGrid() {
        try {
            int i, j;
            for (i = 0; i < gridSize; i++) {
                gridPane.addRow(i);
                for (j = 0; j < gridSize; j++) {
                    gridPane.addColumn(j);
                    Button button = (Button) gridPane.getChildren().get(i * gridSize + j);
                    if (grid[i][j] != 0) {
                        button.setDisable(true);
                        continue;
                    }
                    button.setDisable(false);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void win(Stats stats, String username) {
        centerLabel.setText("You win!");
        gridPane.visibleProperty().set(false);
        try {
            playerScore.setText("Your Score:");
            wins.setText("Wins: " + (stats.getWins() + 1));
            draws.setText("Draws: " + stats.getDraws());
            losses.setText("Losses: " + stats.getLosses());
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void draw(Stats stats, String username) {
        centerLabel.setText("Draw!");
        gridPane.visibleProperty().set(false);
        try {
            playerScore.setText("Your Score:");
            wins.setText("Wins: " + stats.getWins());
            draws.setText("Draws: " + (stats.getDraws() + 1));
            losses.setText("Losses: " + stats.getLosses());
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void lose(Stats stats, String username) {
        centerLabel.setText("You lose!");
        gridPane.visibleProperty().set(false);
        try {
            playerScore.setText("Your Score:");
            wins.setText("Wins: " + stats.getWins());
            draws.setText("Draws: " + stats.getDraws());
            losses.setText("Losses: " + (stats.getLosses() + 1));
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void refreshGrid() {
        try {
            for (int i = 0; i < this.gridSize; i++) {
                for (int j = 0; j < this.gridSize; j++) {
                    Button button = (Button) gridPane.getChildren().get(i * gridSize + j);
                    if (grid[i][j] == 1) {
                        button.setText("X");
                        button.setDisable(true);
                    } else if (grid[i][j] == 2) {
                        button.setText("O");
                        button.setDisable(true);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);

        }
    }

    public void playersJoined() {
        try {
            if (isFull.equals("true")) {
                centerLabel.setText("");

            } else {
                centerLabel.setText("Waiting for players...");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void updatePlayerNames() {
        playerName.setText(player1);
        player2Name.setText(player2);
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        refresh();
    }
}
