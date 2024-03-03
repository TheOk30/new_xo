package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//server class
public class Server {

    public Server() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new java.net.InetSocketAddress(SocketHandler.getHost(), SocketHandler.getPort()));
            System.out.println("Server is running on port " + String.valueOf(SocketHandler.getPort()));

            //creates a new thread per each new connection
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } 
        
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
