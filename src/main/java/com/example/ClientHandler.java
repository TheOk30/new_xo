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
            String message;
            boolean running = true;
            GAME currentgame = null;
            
            //loop recives messeges per specific game
            while (running) {
                try{
                    message = this.clientSocketHandler.Receive();
                    System.out.println("Received message: " + message);
    
                    //closed connection
                    if(message.equals("close")) {
                        System.out.println("client disconected - " + this.clientSocket.getLocalAddress().toString());
                        currentgame.close();
                        games.remove(currentgame);
                        this.clientSocketHandler.close();
                        running = false;
                    }
    
                    //start a new game
                    else if (message.contains("start")){
                        String parts[] = message.split("-");
                        String playerName = parts[1];
                        int gridSize = Integer.parseInt(parts[2]);
                        System.out.println("player name:" + playerName + " gridsize: " + gridSize);
    
                        // look for a waiting game
                        for (GAME game : games) {
                            if (game.getGridSize() == gridSize && !game.isFull()) {
                                this.clientSocketHandler.Send("2");
                                game.setSocket(this.clientSocketHandler);
                                game.setPlayer2(playerName);
                                currentgame = game;
                            }
                        }
    
                        //found an empty game
                        if (currentgame == null) {
                            this.clientSocketHandler.Send("1");
                            currentgame = new GAME(gridSize, playerName, this.clientSocketHandler);
                        }
    
                        //add game to the list of games
                        games.add(currentgame);
                        System.out.println("Game created with size " + gridSize);
                    }
    
                    else if (message.equals("getGridSize")) {
                        this.clientSocketHandler.Send("isFull-" + String.valueOf(currentgame.isFull()));
                    }
    
                    else if (message.equals("isFull")) {
                        this.clientSocketHandler.Send(String.valueOf(currentgame.getGridSize()));
                    }
                    
                    //send the new grid after change
                    else if (message.contains("changeGrid")) {
                        String[] parts = message.split("-");
                        currentgame.changeGrid(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),Integer.parseInt(parts[3]));
                    }
    
                    else if (message.contains("getGrid")) {
                        String[] parts = message.split("-");
                        this.clientSocketHandler.Send(String.valueOf(
                                currentgame.getGrid(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]))));
                    }
    
                    else if (message.contains("getPlayers")) {
                        String[] players = currentgame.getPlayers();
                        System.err.println(players[0].toString());
                        System.err.println(players[1].toString());
    
                        this.clientSocketHandler.Send("getPlayers-" + players[0] + "-" + players[1]);
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
