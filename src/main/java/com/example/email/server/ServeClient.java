package com.example.email.server;

import com.example.email.model.EmailComplete;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServeClient extends Thread{
    private Socket socket;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    @FXML
    TextArea log;

    public ServeClient(Socket socket,TextArea log) {
        setDaemon(true);
        this.socket = socket;
        this.log=log;
    }

    @Override
    public synchronized void start() {
        try {
            inStream = new ObjectInputStream(socket.getInputStream());
            outStream = new ObjectOutputStream(socket.getOutputStream());
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.start();
    }

    @Override
    public void run() {

        while (inStream!=null&&socket!=null){
            try {
                EmailComplete email = (EmailComplete) inStream.readObject();
                log.appendText(email.toString()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println("una connessione con un client è stata interrotta");


        /*
        try {
            String name="";
            FileWriter fileInput = new FileWriter("Email"+name);

        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
