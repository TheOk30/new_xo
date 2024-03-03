package com.example;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.example.models.BaseEntity;
import com.example.models.Player;
import com.example.models.Stats;
import com.example.views.PlayerStatsDB;
import com.example.views.PlayersDB;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Menu {
    private Socket clientSocket;
    private SocketHandler clientSocketHandler;
    private DataOutputStream out;
    private volatile boolean isRunning = true;

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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            } catch (NumberFormatException e) {
                Platform.runLater(() -> error.setText("Error: Grid size must be an integer"));
            }
        }).start();
    }

    private void processGridSize(int gridSize) throws IOException {
        if (gridSize < 3) {
            error.setText("Error: Grid size must be at least 3");
        }

        else if (gridSize > 7) {
            error.setText("Error: Grid size must be at most 7");
        }

        else {
            // Connect to the server
            this.clientSocket = new Socket(SocketHandler.getHost(), SocketHandler.getPort());
            this.clientSocketHandler = new SocketHandler(this.clientSocket);
            GUI gui = new GUI(1, gridSize);
            gui.setGame(this.clientSocketHandler);
            clientSocketHandler.Send("start-" + this.playerName.getText() + "-" + this.gridSize.getText());

            try {
                Player player = new Player(playerName.getText());
                ArrayList<BaseEntity> players = playersDB.selectAll();
                boolean exists = false;
                for (BaseEntity p : players) {
                    if (((Player)p).getUsername().equals(playerName.getText())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    playersDB.insert(player);
                    playersDB.saveChanges();
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }

            /*
             * // On close
             * App.getPrimaryStage().setOnCloseRequest(e -> {
             * this.clientSocketHandler.Send("quit");
             * try {
             * this.clientSocket.close();
             * } catch (Exception ex) {
             * System.out.println(ex.toString());
             * }
             * });
             */

            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameWindow.fxml"));
            loader.setController(gui);
            App.getPrimaryStage().setScene(new Scene(loader.load(), 640, 480));
            App.getPrimaryStage().setOnCloseRequest(e -> {
                clientSocketHandler.Send("close");
            });

            new Thread(() -> {
                Player player = playersDB.selectByUsername(playerName.getText());
                Stats stats = playerStatsDB.selectByPlayerId(player.getId());
                while (isRunning) {
                    String message = clientSocketHandler.Receive();

                    System.out.println("message is" + message);

                    Platform.runLater(() -> {
                        if (message.equals("1") || message.equals("2")) {
                            gui.setValue(Integer.parseInt(message));
                        }

                        else if (message.equals("enableGrid")) {
                            gui.enableGrid();
                        }

                        else if (message.equals("disableGrid")) {
                            gui.disableGrid();
                        }

                        else if (message.equals("win")) {
                            try {
                                gui.win(stats, playerName.getText());
                            }

                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        else if (message.equals("lose")) {
                            try {
                                gui.lose(stats, playerName.getText());
                            }

                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        else if (message.equals("draw")) {
                            try {
                                gui.draw(stats, playerName.getText());
                            }

                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        else if (message.contains("refreshGrid")) {
                            String[] parts = message.split("-");
                            gui.updateGrid(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                                    Integer.parseInt(parts[3]));
                        }

                        else if (message.equals("refresh")) {
                            gui.refresh();
                        }

                        else if (message.equals("createGrid")) {
                            gui.createGrid();
                        }

                        else if (message.contains("getPlayers")) {
                            String[] parts = message.split("-");
                            gui.setPlayer1(parts[1]);
                            gui.setPlayer2(parts[2]);
                            gui.updatePlayerNames();
                        }

                        else if (message.contains("isFull")) {
                            String[] parts = message.split("-");
                            gui.setIsFull(parts[1]);
                            gui.playersJoined();
                        }

                        else if (message.equals("close")) {
                            this.clientSocketHandler.close();
                            try {
                                this.clientSocket.close();
                            }

                            catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            this.isRunning = false;
                        }
                    });
                }

            }).start();
        }
    }
}
