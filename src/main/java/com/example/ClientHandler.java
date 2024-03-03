package com.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

//class per each thread of the game created for each new connection
public class ClientHandler implements Runnable {
    private Socket clientSocket; //the actual socket
    private SocketHandler clientSocketHandler; //the socket handler with the send receive and close functions

    //Array that holds all of the games currently playing or waiting
    private static ArrayList<GAME> games = new ArrayList<GAME>();

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientSocketHandler = new SocketHandler(clientSocket);
    }

    @Override
    public void run() {
        try {
            GameElements element;
            boolean running = true;
            GAME currentgame = null;
            
            //loop recives messeges per specific game
            while (running) {
                try{
                    element = this.clientSocketHandler.Receive();
                    String topic = element.getTopic();
                    System.out.println("Received message: " + topic);
    
                    //closed connection
                    if(topic.equals("close")) {
                        System.out.println("client disconected - " + this.clientSocket.getLocalAddress().toString());
                        currentgame.close();
                        games.remove(currentgame);
                        this.clientSocketHandler.close();
                        running = false;
                    }
    
                    //start a new game
                    else if (topic.equals("start")){
                        String playerName = element.getPlayerName();
                        int gridSize = Integer.parseInt(element.getGridSize());
                        System.out.println("player name:" + playerName + " gridsize: " + gridSize);
    
                        // look for a waiting game
                        for (GAME game : games) {
                            if (game.getGridSize() == gridSize && !game.isFull()) {
                                this.clientSocketHandler.Send(new GameElements("PlayerNum", "2"));
                                game.setSocket(this.clientSocketHandler);
                                game.setPlayer2(playerName);
                                currentgame = game;
                                games.remove(game);
                                break;
                            }
                        }
    
                        //found an empty game
                        if (currentgame == null) {
                            this.clientSocketHandler.Send(new GameElements("PlayerNum", "1"));
                            currentgame = new GAME(gridSize, playerName, this.clientSocketHandler);
                        }
    
                        //add game to the list of games
                        games.add(currentgame);
                        System.out.println("Game created with size " + gridSize);
                    }
    
                    else if (topic.equals("getGridSize")) {
                        this.clientSocketHandler.Send(new GameElements(String.valueOf(currentgame.getGridSize())));
                    }
    
                    else if (topic.equals("isFull")) {
                        this.clientSocketHandler.Send(new GameElements("isFull",String.valueOf(currentgame.isFull())));
                    }
                    
                    //send the new grid after change
                    else if (topic.equals("changeGrid")) {
                        currentgame.changeGrid(Integer.parseInt(element.getX()), Integer.parseInt(element.getY()),Integer.parseInt(element.getValue()));
                    }
    
                    /*else if (topic.equals("getGrid")) {
                        this.clientSocketHandler.Send(String.valueOf(
                                currentgame.getGrid(Integer.parseInt(element.getX()), Integer.parseInt(element.getY()))));
                    }*/
    
                    else if (topic.equals("getPlayers")) {
                        String[] players = currentgame.getPlayers();
                        System.err.println(players[0].toString());
                        System.err.println(players[1].toString());
    
                        this.clientSocketHandler.Send(new GameElements("getPlayers",players[0],players[1]));
                    }
                }

                catch (Exception e) {
                    System.out.println("Error: " + e);
                    try {
                        clientSocketHandler.close();
                        System.out.println("Client disconnected from " + clientSocket.getInetAddress());
                    } 
                    
                    catch (Exception e2) {
                        System.out.println("Error: " + e2);
                    }
                } 
            }
        }

        catch(Exception e) {
        
            e.printStackTrace();
        }

        finally {
            try {
                this.clientSocketHandler.close();
                this.clientSocket.close();
            } 
            
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    } 
}
