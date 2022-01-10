package com.example.email.Server;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener extends Thread {
    private int port;
    @FXML
    TextArea log;
    public ServerListener(TextArea log, int port) {
        this.log = log;
        this.port=port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true){
                Socket socket = serverSocket.accept();
                log.appendText("-New client connected");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
