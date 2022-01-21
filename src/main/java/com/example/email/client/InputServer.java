package com.example.email.client;

import com.example.email.model.EmailComplete;
import com.example.email.model.Utente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class InputServer extends Thread{
    private Utente utente;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;


//TODO cancellare questa classe èerchè non serve più
    public InputServer(Utente utente,Socket socket,ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        setDaemon(true);
        this.utente = utente;
        this.socket=socket;
        this.inputStream=inputStream;
        this.outputStream=outputStream;
        try {
            outputStream.writeObject(utente.getEmailAddress());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void run() {
        while(true) {
            while (inputStream != null&&socket!=null) {
                /*mi metto a dormire in attesa di mail dal server*/
                try {
                    System.out.print("sono in attesa di leggere mail:   ");
                    EmailComplete email = (EmailComplete) inputStream.readObject();
                    utente.addEmail(email);
                    System.out.println("Email ricevuta -> "+email.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }


            while(inputStream==null||socket==null) {
                /*immagino blocco la grafica e muoio (io thread)*/
                System.out.println("cazzo non sono connesso al server mo che faccio?");
            }
        }
    }


}
