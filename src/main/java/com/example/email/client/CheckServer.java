package com.example.email.client;

import com.example.email.model.EmailComplete;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class CheckServer extends Thread{
    private BooleanProperty warning;
    private BooleanProperty warningInvio;
    private Boolean emails_recived;
    private ClientController clientController;


    public CheckServer(BooleanProperty warning,BooleanProperty warningInvio,Boolean emails_recived, ClientController clientController) {
        this.setDaemon(true);
        this.warning = warning;
        this.warningInvio = warningInvio;
        this.emails_recived = emails_recived;
        this.clientController = clientController;
    }





    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Socket socket = ClientController.startServerConnection("localhost", 6868);
            if (socket == null) {
                warning.set(true);
                System.out.println("Not Conneccted with server");
            }else{
                if (!emails_recived){
                    clientController.getEmail(socket);
                    emails_recived=true;
                    warning.set(false);
                    warningInvio.set(false);
                }else{
                    warning.set(false);
                    warningInvio.set(false);
                    try {
                        OutputStream outStream = socket.getOutputStream();
                        InputStream inStream = socket.getInputStream();
                        ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
                        ObjectInputStream objInStream = new ObjectInputStream(inStream);

                        objOutStream.writeObject(clientController.utente.getEmailAddress());
                        objOutStream.writeObject("CHECK");
                        objOutStream.flush();
                        System.out.println("CHECK");
                        String answer = (String) objInStream.readObject();
                        if(answer.equals("OK")){
                            objOutStream.writeObject(clientController.utente.getLocalDateTimeLastEmailInbox());
                            ArrayList<EmailComplete> newMails = (ArrayList<EmailComplete>) objInStream.readObject();
                            if (newMails==null) System.out.println("No new mails");
                            else {
                                clientController.utente.inbox.addAll(newMails);
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }


            }

        }
    }




}
