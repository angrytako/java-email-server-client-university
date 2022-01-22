package com.example.email.client;

import com.example.email.model.EmailComplete;
import com.example.email.model.Utente;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class OutToServer extends Thread{
    private Socket socket;
    private EmailComplete email;
    private Utente user;
    public OutToServer(EmailComplete email, Utente user,Socket socket) {
        this.email = email;
        this.user = user;
        this.socket=socket;
    }


    @Override
    public void run() {
        try {
            OutputStream outStream = socket.getOutputStream();
            ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
            InputStream inStream = socket.getInputStream();
            ObjectInputStream objInStream = new ObjectInputStream(inStream);
            objOutStream.writeObject(user.getEmailAddress());
            objOutStream.writeObject("SEND");
            objOutStream.flush();
            String answer = (String) objInStream.readObject();
            if(answer.equals("OK")){
                objOutStream.writeObject(email);
                objOutStream.flush();
            }else {
                //TODO error handling
                return;
            }
            answer = (String) objInStream.readObject();
            if(answer.equals("OK")) {
                EmailComplete sentEmail = (EmailComplete) objInStream.readObject();
                System.out.println(sentEmail.toString());
                user.sentEmails.add(sentEmail);
                if(sentEmail.getDestinatari().contains(user.getEmailAddress()))
                    user.inbox.add(sentEmail);
            }else{
                //TODO error handling
                return;
            }

            } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
