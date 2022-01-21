package com.example.email.client;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

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

    public CheckServer(BooleanProperty warning,BooleanProperty warningInvio, Boolean emails_recived) {
        setDaemon(true);
        this.warning = warning;
        this.warningInvio=warningInvio;
        this.emails_recived=emails_recived;
    }



    @Override
    public void run() {
        while(true) {
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
                        ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
                        //TODO chiede se ci sono email nuove
                        objOutStream.writeObject("CHECK");
                        objOutStream.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




}
