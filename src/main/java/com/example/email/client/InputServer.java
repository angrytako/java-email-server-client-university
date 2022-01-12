package com.example.email.client;

import com.example.email.model.Utente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class InputServer extends Thread{
    private Utente utente;
    private Socket socket;
    private ObjectInputStream inputStream;


    public InputServer(Utente utente,Socket socket,ObjectInputStream inputStream) {
        setDaemon(true);
        this.utente = utente;
        this.socket=socket;
        this.inputStream=inputStream;
    }


    @Override
    public void run() {
        while(true) {
            while (inputStream != null) {
                /*mi metto a dormire in attesa di input*/
                try {
                    System.out.println(inputStream.readObject());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

/*
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print(utente.getEmailAddress() + " In attesa, state:");
                System.out.println(socket.isConnected());  */
            }
            while(inputStream==null) {
                /*immagino blocco la grafica e muoio (io thread)*/
                System.out.println("cazzo non sono connesso al server mo che faccio?");
            }
        }
    }


}
