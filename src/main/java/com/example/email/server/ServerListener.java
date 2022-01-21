package com.example.email.server;

import com.example.email.model.SocketActive;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ServerListener extends Thread {
    private ExecutorService executor;
    private int port;
    @FXML
    TextArea log;

    public ServerListener(TextArea log, int port) {
        this.setDaemon(true);
        this.log = log;
        this.port=port;
        this.executor = Executors.newFixedThreadPool(10, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        } );

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

                ServeClient client = new ServeClient(socket,log);

                executor.execute(client);

//                Thread thread = new Thread(client);
//                thread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("ciao");
            executor.shutdown();
            if (serverSocket!=null)
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            while (!executor.isTerminated()) {
                System.out.println("aaaaaaa");
            }
            System.out.println("Finished all thread");
        }
    }


}
