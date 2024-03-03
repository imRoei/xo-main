package com.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
// import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class sockProtocol {
    // private static final int MAX_BUFFER_SIZE = 100;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private static int PORT = 1234;
    private static String HOST = "localhost";

    public sockProtocol(Socket socket) {
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public GameElements res() {
        try {
            int length = this.in.readInt(); // Read the length of the byte array
            byte[] bytes = new byte[length];
            this.in.readFully(bytes); // Read the byte array
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectStream = new ObjectInputStream(byteStream);
            return (GameElements) objectStream.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public void send(GameElements res) {
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

    public void close() {
        try {
            socket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public boolean isClosed() {
        return this.socket.isClosed();
    }

    public static String getHost() {
        return HOST;
    }

    public static int getPort() {
        return PORT;
    }
}
