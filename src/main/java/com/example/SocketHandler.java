package com.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//class that controls socket communication between server and client with all the methods needed
public class SocketHandler {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private static final int PORT = 5555;
    private static final String HOST = "localhost";

    // create the socket Handler
    public SocketHandler(Socket socket) {
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // recieve data function from socket
    public GameElements Receive() {
        try {
            int length = this.in.readInt(); // Read the length of the byte array
            byte[] bytes = new byte[length];
            this.in.readFully(bytes); // Read the byte array
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectStream = new ObjectInputStream(byteStream);
            return (GameElements) objectStream.readObject();
        } 
        
        catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    // send data function to socket
    public void Send(GameElements res) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(res);
            objectStream.flush();
            byte[] bytes = byteStream.toByteArray();
            this.out.writeInt(bytes.length); // Write the length of the byte array
            this.out.write(bytes); // Write the byte array
            this.out.flush();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // close the socket
    public void close() {
        try {
            socket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    // check if the socket is closed
    public boolean isClosed() {
        return this.socket.isClosed();
    }

    // get the Host
    // Allows for the host to be written just once
    public static String getHost() {
        return HOST;
    }

    // get the port
    // Allows for the port to be written just once
    public static int getPort() {
        return PORT;
    }

}
