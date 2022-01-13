package com.example.email.server;

import com.example.email.model.EmailComplete;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
public class ServeClient extends Thread{
    private Socket socket;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private String utente=null;
    @FXML
    TextArea log;
    public ServeClient(Socket socket,TextArea log) {
        setDaemon(true);
        this.socket = socket;
        this.log=log;

        try {
            outStream = new ObjectOutputStream(socket.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(socket.getInputStream());
            utente = (String) inStream.readObject();
            log.appendText(utente+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void run() {




        /*invio al client le sue mail
        try {
            ObjectInputStream storico = new ObjectInputStream(new FileInputStream("src/main/resources/email/sent_"+utente+".txt"));
            EmailComplete email;
            do {
                email = (EmailComplete) storico.readObject();
                if(email != null){
                    System.out.println(email);
                  //invio mail
                }
            } while (email != null);

            storico.close();

        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("error");
        }


        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/





        //ricezione delle mail
        while (inStream!=null&&socket!=null){
            try {
                EmailComplete email = (EmailComplete) inStream.readObject();
                log.appendText(email.toString()+"\n");
                FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/email/sent_"+utente+".txt", true);
                ObjectOutputStream storico = new ObjectOutputStream(fileOutputStream);
                storico.writeObject(email);
                storico.close();
                
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