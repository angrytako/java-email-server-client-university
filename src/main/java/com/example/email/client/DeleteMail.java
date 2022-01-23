package com.example.email.client;

import com.example.email.model.EmailComplete;
import com.example.email.model.Utente;
import javafx.beans.property.BooleanProperty;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class DeleteMail extends Thread{

    private EmailComplete emailToDelete;
    private Utente utente;
    private  BooleanProperty warning;
    public DeleteMail(BooleanProperty warning,EmailComplete emailToDelete, Utente utente) {

        this.emailToDelete = emailToDelete;
        this.utente = utente;
        this.warning=warning;
    }

    @Override
    public void run() {
        Socket socket = ClientController.startServerConnection("localhost", 6868);
        if (socket == null) {
            warning.set(true);
            System.out.println("Not Conneccted with server");
        }else{
            System.out.println(emailToDelete);
            try {
                OutputStream outStream = socket.getOutputStream();
                InputStream inStream = socket.getInputStream();
                ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
                ObjectInputStream objInStream = new ObjectInputStream(inStream);

                objOutStream.writeObject(utente.getEmailAddress());

                String action=null;
                if(emailToDelete.getMittente().equals(utente.getEmailAddress())){
                    action="DELETE SENT";
                    String[] receivers = emailToDelete.getDestinatari().split(",");
                    for(String receiver : receivers){
                        if(receiver.equals(utente.getEmailAddress())) action="DELETE";
                    }
                }
                else{
                    //email ricevuta
                    action="DELETE INBOX";
                }
                System.out.println("Azione: "+action);
                objOutStream.writeObject(action);
                objOutStream.flush();
                String answer = (String) objInStream.readObject();
                if(answer.equals("OK")){
                    //invio id mail da cancellare
                    objOutStream.writeObject(emailToDelete.getID());
                    //leggo cancellatted, cancello la mail
                    //else errore server faccio vedere warning
                    String result = (String) objInStream.readObject();
                    System.out.println(result);
                    if(result.equals("CANCELLATED")){
                        //la mail è stata cancellata aggiorno il modello
                        utente.deleteEmail(emailToDelete);

                        System.out.println("email cancellata");
                    }else if(result.equals("ABORT")){
                        System.out.println("Errore nella cancellazione del file lato server");
                    }else  warning.set(true);


                }else{
                    warning.set(true);
                    System.out.println("Error with server comunication");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


        System.out.println("Sono il Thread, sono arrivato alla fine");
    }
}
