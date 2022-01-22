package com.example.email.server;

import com.example.email.model.DAO;
import com.example.email.model.EmailComplete;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class ServeClient implements Runnable{
    private final static String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private enum RequestType {DELETE,DELETE_INBOX,DELETE_SENT,CHECK, ERROR, SEND_EMAIL, GET_ALL, GET_IN, GET_OUT, GET_ALL_IN, GET_ALL_OUT };
    private Socket socket;
    private ObjectInputStream inObjStream;
    private OutputStream outputStream;
    private InputStream inputStream;
    private ObjectOutputStream outObjStream;
    private String utente;

    @FXML
    TextArea log;

    public ServeClient(Socket socket,TextArea log) {
        this.socket = socket;
        this.log=log;

        try {
            outputStream = socket.getOutputStream();
            outObjStream = new ObjectOutputStream(outputStream);
            inputStream = socket.getInputStream();
            inObjStream = new ObjectInputStream(inputStream);
            utente = (String) inObjStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }




    public void run() {
    //protocol:
    //send your email adress
    //continue with text request and await text acknowledgement:
        // SEND -> [OK,ERROR ...]
        // GET ALL -> [OK, ERROR ...]
        // GET IN FROM <EMAIL_ID> -> [OK, ERROR ...]
        // GET OUT FROM <EMAIL_ID> -> [OK, ERROR ...]
        //if error is sent, socket gets terminated, else:
        //case SEND -> OK
            //client sends an EmailComplete
            //server writes in the appropriate files and sends back OK and then the sentEmail or ERROR at the end
        //case GET ALL -> OK
            //server first sends an ArrayList<EmailComplete> of all inbox emails,
            // then an ArrayList<EmailComplete> of all sent emails;
            // in case of error sends null and closes connection
        //case GET IN FROM <EMAIL_ID> -> OK
            //server searches for <EMAIL_ID> in inbox file and sends an ArrayList<EmailComplete>; in case of error sends null and closes connection
            //no <EMAIL_ID> is interpreted as "give all inbox emails"
        //case GET OUT FROM <EMAIL_ID> -> OK
            //server searches for <EMAIL_ID> in sent file and sends an ArrayList<EmailComplete>; in case of error sends null and closes connection
            //no <EMAIL_ID> is interpreted as "give all sent emails"

    try{

        String request = (String) inObjStream.readObject();
        RequestType actionRequested = parseRequest(request);
        System.out.println(actionRequested+" "+utente);
        switch (actionRequested){
            case SEND_EMAIL: {
                log.appendText("\n"+utente+": SENDING EMAIL");
                outObjStream.writeObject("OK");
                EmailComplete email = (EmailComplete) inObjStream.readObject();
                //this operation also sets the email's ID
                emailSending(email);
                outObjStream.writeObject("OK");
                outObjStream.writeObject(email);
                break;
            }
            case GET_ALL: {
                log.appendText("\n"+utente+": Get all emails");
                outObjStream.writeObject("OK");
                outObjStream.flush();
                ArrayList<EmailComplete> inboxEmails = DAO.getAllEmails(utente,true);
                outObjStream.writeObject(inboxEmails);
                ArrayList<EmailComplete> sentEmails = DAO.getAllEmails(utente,false);
                outObjStream.writeObject(sentEmails);
                break;
            }
            case GET_ALL_IN: {
                log.appendText("\n"+utente+": Get all inbox emails");
                ArrayList<EmailComplete> inboxEmails = DAO.getAllEmails(utente,true);
                outObjStream.writeObject(inboxEmails);
                break;
            }
            case GET_ALL_OUT: {
                log.appendText("\n"+utente+": Get all sent emails");
                ArrayList<EmailComplete> sentEmails = DAO.getAllEmails(utente,false);
                outObjStream.writeObject(sentEmails);
                break;
            }
            case GET_IN: {
                ArrayList<EmailComplete> inboxEmails = DAO.getAllEmails(utente,true);
                String ID = getRequestEmailID(request);
                log.appendText("\n"+utente+": Get all inbox emails after id " + ID);
                outObjStream.writeObject(getEmailsAfterID(inboxEmails,ID));
                break;
            }
            case GET_OUT: {
                ArrayList<EmailComplete>  sentEmails = DAO.getAllEmails(utente,false);
                String ID = getRequestEmailID(request);
                log.appendText("\n"+utente+": Get all sent emails after id " + ID);
                outObjStream.writeObject(getEmailsAfterID(sentEmails,ID));
                break;
            }
            case CHECK:{
                outObjStream.writeObject("OK");
                outObjStream.flush();
                LocalDateTime lastEmailInbox = (LocalDateTime) inObjStream.readObject();
                ArrayList<EmailComplete> newMails = DAO.ceckNewEmail(utente,lastEmailInbox);
                if (newMails!=null)  log.appendText("\n"+utente+": RECEIVING"+newMails.toString());
                outObjStream.writeObject(newMails);
                break;
            }
            case DELETE:{
                outObjStream.writeObject("OK");
                outObjStream.flush();
                //ricevo id mail da cancellare
                String id = (String) inObjStream.readObject();
                //se la cancello invio cancellated
                String message="CANCELLATED";
                if(DAO.deleteEmail(utente,id,false)){
                    log.appendText("\n"+utente+": CANCELLATED SENT email");
                }else{
                    message="ABORT";
                    log.appendText("\n"+utente+": CANCELLATED SENT error");
                }
                if(DAO.deleteEmail(utente,id,true)){
                    log.appendText("\n"+utente+": CANCELLATED INBOX");
                }else{
                    message="ABORT";
                    log.appendText("\n"+utente+": CANCELLATED INBOX error");
                }
                outObjStream.writeObject(message);

                break;
            }
            case DELETE_INBOX:{
                outObjStream.writeObject("OK");
                outObjStream.flush();
                //ricevo id mail da cancellare
                String id = (String) inObjStream.readObject();
                //se la cancello invio cancellated
                if(DAO.deleteEmail(utente,id,true)){
                    outObjStream.writeObject("CANCELLATED");
                    log.appendText("\n"+utente+": CANCELLATED INBOX");
                }else{
                    outObjStream.writeObject("ABORT");
                    log.appendText("\n"+utente+": CANCELLATED INBOX error");
                }

                break;
            }
            case DELETE_SENT:{
                outObjStream.writeObject("OK");
                outObjStream.flush();
                //ricevo id mail da cancellare
                String id = (String) inObjStream.readObject();
                //se la cancello invio cancellated
                if(DAO.deleteEmail(utente,id,false)){
                    outObjStream.writeObject("CANCELLATED");
                    log.appendText("\n"+utente+": CANCELLATED SENT email");
                }else{
                    outObjStream.writeObject("ABORT");
                    log.appendText("\n"+utente+": CANCELLATED SENT error");
                }
                break;
            }
            case ERROR: {
                log.appendText("\n"+utente+": Bad request");
                outObjStream.writeObject("ERROR BAD REQUEST");
                break;
            }
        }
    }
    catch (Exception e){
        try {
            System.out.println(e);
            log.appendText("\n"+utente+": Unexpected error");
            outObjStream.writeObject("ERROR UNKNOWN");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    finally {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }

    private RequestType parseRequest(String request){
        if(request.contains("SEND"))
            return RequestType.SEND_EMAIL;
        else if(request.equals("GET ALL"))
            return RequestType.GET_ALL;
        else if(request.equals("CHECK"))
            return RequestType.CHECK;
        else if(request.equals("DELETE"))
            return RequestType.DELETE;
        else if(request.equals("DELETE INBOX"))
            return RequestType.DELETE_INBOX;
        else if(request.equals("DELETE SENT"))
            return RequestType.DELETE_SENT;
        else if(request.contains("GET IN FROM ")){
            if(request.split(" ").length == 3 || request.split(" ")[3].equals(""))
                return RequestType.GET_ALL_IN;
            else return RequestType.GET_IN;
        }
        else if(request.contains("GET OUT FROM")){
            if(request.split(" ").length == 3 || request.split(" ")[3].equals(""))
                return RequestType.GET_ALL_OUT;
            else return RequestType.GET_OUT;
        }
        else return RequestType.ERROR;
    }


    private String getRequestEmailID(String request){
        return  request.split(" ")[3];
    }

    private ArrayList<EmailComplete> getEmailsAfterID(ArrayList<EmailComplete> emails, String ID){
        boolean found = false;
        ArrayList<EmailComplete> filteredEmails = new ArrayList<>();
        for(EmailComplete email : emails){
            if(found)
                filteredEmails.add(email);
            if(email.getID().equals(ID))
                found = true;
        }
        return filteredEmails;
    }

    private String[] getReceivers(String recivers){
        return recivers.split(",");
    }


    private boolean validateReceivers(String recivers){
        if(recivers.length() == 0)
            return false;
        String[] emails = recivers.split(",");
        for (String email : emails){
            if(!email.trim().toLowerCase().matches(EMAIL_REGEX))
                return false;
        }
        return true;
    }


    private void emailSending(EmailComplete email) throws IOException, ClassNotFoundException {
        if(!validateReceivers(email.getDestinatari()))
            return;
        String[] receivers = getReceivers(email.getDestinatari());
        UUID uuid = UUID.randomUUID();
        //in any case ID is set serverSide
        email.setID(uuid.toString());
        //same for date
        email.setData(LocalDateTime.now());
        //putting email in receiver inbox
        for(String receiver : receivers){
            DAO.writeEmailToFile(receiver, email,false);
        }
        // putting email in sender send file
        DAO.writeEmailToFile(utente, email,true);
    }








}