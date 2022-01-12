package com.example.email.client;

import com.example.email.model.EmailComplete;
import com.example.email.model.Utente;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OutToServer extends Thread{
    private Socket socket;
    private EmailComplete email;
    private ObjectOutputStream outputStream;

    public OutToServer(EmailComplete email, Socket socket, ObjectOutputStream outputStream) {
        setDaemon(true);
        this.email = email;
        this.socket = socket;
        this.outputStream=outputStream;
    }


    @Override
    public void run() {
        try {
            outputStream.writeObject(email);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
