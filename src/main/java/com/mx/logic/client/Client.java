package com.mx.logic.client;


import com.mx.data.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private static Client instance;
    private Account user;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private Client() {
        try {
            socket = new Socket("localhost", 2026);
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public Account getUser() {
        return user;
    }

    public void clearUser() {
        user = null;
    }

    public void writeObject(String request, Account user) {
        try {
            outputStream.writeObject(request);
            outputStream.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeObject(String request, Technique tech) {
        try {
            outputStream.writeObject(request);
            outputStream.writeObject(tech);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeObject(String request, Providers provider) {
        try {
            outputStream.writeObject(request);
            outputStream.writeObject(provider);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeObject(String request, Supplies supply) {
        try {
            outputStream.writeObject(request);
            outputStream.writeObject(supply);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeObject(String request, Selling selling) {
        try {
            outputStream.writeObject(request);
            outputStream.writeObject(selling);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeObject(String request) {
        try {
            outputStream.writeObject(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeObject(Integer request) {
        try {
            outputStream.writeObject(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean inputBoolean() {
        try {
            return inputStream.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean inputBooleanUser() {
       try {
            if (inputStream.readBoolean()) {
                this.user = (Account) inputStream.readObject();
            } else {
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public <T> ArrayList<T> inputList() {
        ArrayList resList = null;
        try {
            resList = (ArrayList<T>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resList;
    }

    public Providers inputProvider(){
        Providers provider = null;
        try{
            provider = (Providers)inputStream.readObject();
        }catch (IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }
        return provider;
    }
}
