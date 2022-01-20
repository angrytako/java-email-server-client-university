package com.example.email.server;

import com.example.email.model.SocketActive;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerListener extends Thread {
    private int port;
    @FXML
    TextArea log;

    public ServerListener(TextArea log, int port) {
        setDaemon(true);
        this.log = log;
        this.port=port;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
             serverSocket = new ServerSocket(port);
            log.appendText("-Server ON  -->   ");
            log.appendText(String.valueOf(serverSocket.toString())+"\n");
            while(true){
                Socket socket = serverSocket.accept();
                /* TODO questo dovrà essere implementato con thread pool*/
                ServeClient client = new ServeClient(socket,log);
                client.start();
                /*
                ServeClient client = new ServeClient(socket,log,socketsActive,socketActive);
                client.start();
                */


            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (serverSocket!=null)
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


}
