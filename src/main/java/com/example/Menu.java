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
            clientSocketHandler.Send(new GameElements("start",this.playerName.getText(),this.gridSize.getText()));

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
                clientSocketHandler.Send(new GameElements("close"));
            });

            new Thread(() -> {
                Player player = playersDB.selectByUsername(playerName.getText());
                Stats stats = playerStatsDB.selectByPlayerId(player.getId());

                while (isRunning) {
                    GameElements element = clientSocketHandler.Receive();
                    String topic = element.getTopic();

                    System.out.println("message is" + topic);

                    Platform.runLater(() -> {
                        if (topic.equals("PlayerNum")) {
                            gui.setValue(Integer.parseInt(element.getPlayerNumber()));
                        }

                        else if (topic.equals("enableGrid")) {
                            gui.enableGrid();
                        }

                        else if (topic.equals("disableGrid")) {
                            gui.disableGrid();
                        }

                        else if (topic.equals("win")) {
                            try {
                                gui.win(stats, playerName.getText());
                            }

                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        else if (topic.equals("lose")) {
                            try {
                                gui.lose(stats, playerName.getText());
                            }

                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        else if (topic.equals("draw")) {
                            try {
                                gui.draw(stats, playerName.getText());
                            }

                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        else if (topic.equals("refreshGrid")) {
                            gui.updateGrid(Integer.parseInt(element.getX()), Integer.parseInt(element.getY()),
                                    Integer.parseInt(element.getValue()));
                        }

                        else if (topic.equals("refresh")) {
                            gui.refresh();
                        }

                        else if (topic.equals("createGrid")) {
                            gui.createGrid();
                        }

                        else if (topic.equals("getPlayers")) {
                            gui.setPlayer1(element.getPlayer1Name());
                            gui.setPlayer2(element.getPlayer2Name());
                            gui.updatePlayerNames();
                        }

                        else if (topic.equals("isFull")) {
                            gui.setIsFull(element.getIsfull());
                            gui.playersJoined();
                        }

                        else if (topic.equals("close")) {
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
