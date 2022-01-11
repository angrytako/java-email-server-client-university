package com.example.email.client;

import com.example.email.model.Utente;

import java.io.IOException;
import java.net.Socket;

public class ServerConnession extends Thread{
    private Utente utente;
    private String host;
    private int port;
    private Socket socket;
    private Boolean state=false; //true Connession active, False Connession off


    public ServerConnession(Utente utente, String host, int port) {
        setDaemon(true);
        this.utente = utente;
        this.host = host;
        this.port = port;
    }

    @Override
    public synchronized void start() {
        state=startServerConnession();
        super.start();
    }



    @Override
    public void run() {
        while (true) {
            System.out.println(state);
            while (state) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print(utente.getEmailAddress() + " In attesa, state:");
                state=socket.isConnected();
                System.out.println(socket.isConnected());
            }
            while(!state){
                state=startServerConnession();
            }

        }
    }

    private Boolean startServerConnession() {
        try {
            socket = new Socket(host, port);
            System.out.println(socket);
            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
            // e.printStackTrace();
        }
    }
}
