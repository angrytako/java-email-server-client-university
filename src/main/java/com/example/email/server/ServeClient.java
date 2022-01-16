package com.example.email.server;

import com.example.email.model.EmailComplete;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServeClient extends Thread{
    private final static String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private enum RequestType { ERROR, SEND_EMAIL, GET_ALL, GET_IN, GET_OUT, GET_ALL_IN, GET_ALL_OUT };
    private Socket socket;
    private ObjectInputStream inObjStream;
    private OutputStream outputStream;
    private InputStream inputStream;
    private ObjectOutputStream outObjStream;
    private String utente=null;
    @FXML
    TextArea log;
    public ServeClient(Socket socket,TextArea log) {
        setDaemon(true);
        this.socket = socket;
        this.log=log;

        try {
            outputStream = socket.getOutputStream();
            outObjStream = new ObjectOutputStream(outputStream);
            outObjStream.flush();
            inputStream = socket.getInputStream();
            inObjStream = new ObjectInputStream(inputStream);
            utente = (String) inObjStream.readObject();
            log.appendText(utente+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void run() {
    //protocol:
    //start with text request and text acknowledgement:
        // SEND -> [OK,ERROR ...]
        // GET ALL -> [OK, ERROR ...]
        // GET IN FROM <EMAIL_ID> -> [OK, ERROR ...]
        // GET OUT FROM <EMAIL_ID> -> [OK, ERROR ...]
        //if error is sent, socket gets terminated, else:
        //case SEND -> OK
            //client sends an EmailComplete
            //server writes in the appropriate files and sends back OK or ERROR at the end
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
        Scanner sc = new Scanner(inputStream);
        String request = sc.nextLine();
        PrintWriter out = new PrintWriter(outputStream);
        RequestType actionRequested = parseRequest(request);
        switch (actionRequested){
            case SEND_EMAIL: {
                EmailComplete email = (EmailComplete) inObjStream.readObject();
                emailSending(email);
                out.println("OK");
                break;
            }
            case GET_ALL: {
                ArrayList<EmailComplete> inboxEmails = getAllInboxEmails();
                outObjStream.writeObject(inboxEmails);
                ArrayList<EmailComplete> sentEmails = getAllSentEmails();
                outObjStream.writeObject(sentEmails);
                break;
            }
            case GET_ALL_IN: {
                ArrayList<EmailComplete> inboxEmails = getAllInboxEmails();
                outObjStream.writeObject(inboxEmails);
                break;
            }
            case GET_ALL_OUT: {
                ArrayList<EmailComplete> sentEmails = getAllSentEmails();
                outObjStream.writeObject(sentEmails);
                break;
            }
            case GET_IN: {
                ArrayList<EmailComplete> inboxEmails = getAllInboxEmails();
                int ID = getRequestEmailID(request);
                outObjStream.writeObject(getEmailsAfterID(inboxEmails,ID));
                break;
            }
            case GET_OUT: {
                ArrayList<EmailComplete>  sentEmails = getAllSentEmails();
                int ID = getRequestEmailID(request);
                outObjStream.writeObject(getEmailsAfterID(sentEmails,ID));
                break;
            }
        }
    }
    catch (Exception e){
        PrintWriter out = new PrintWriter(outputStream);
        out.println("ERROR UNKNOWN");
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
        else if(request.contains("GET ALL"))
            return RequestType.GET_ALL;
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


    private int getRequestEmailID(String request){
        return Integer.parseInt(request.split(" ")[3]);
    }

    private ArrayList<EmailComplete> getEmailsAfterID(ArrayList<EmailComplete> emails, int ID){
        boolean found = false;
        ArrayList<EmailComplete> filteredEmails = new ArrayList<>();
        for(EmailComplete email : emails){
            if(found)
                filteredEmails.add(email);
            if(email.getID().intValue() == ID)
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
        String senderFilePath = "src/main/resources/email/sent_" + utente + ".txt";
        if(!validateReceivers(email.getDestinatari()))
            return;
        String[] receivers = getReceivers(email.getDestinatari());
        //putting email in receiver inbox
        for(String receiver : receivers){
            String receiverFilePath = "src/main/resources/email/inbox_" + receiver + ".txt";
            writeEmailToFile(receiverFilePath, email);
        }
        // putting email in sender send file
        writeEmailToFile(senderFilePath, email);
    }


    private void writeEmailToFile(String filePath, EmailComplete email) throws IOException, ClassNotFoundException {
        File userFile = new File(filePath);
        if(userFile.exists() && !userFile.isDirectory()){
            FileInputStream in = new FileInputStream(userFile);
            ObjectInputStream inObjs = new ObjectInputStream(in);
            ArrayList<EmailComplete> availableEmails = (ArrayList<EmailComplete>)inObjs.readObject();
            availableEmails.add(email);
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            ObjectOutputStream storico = new ObjectOutputStream(fileOutputStream);
            storico.writeObject(availableEmails);
            storico.flush();
            storico.close();
            in.close();
            fileOutputStream.close();
        }
        else {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            ObjectOutputStream storico = new ObjectOutputStream(fileOutputStream);
            ArrayList<EmailComplete> arr = new ArrayList<EmailComplete>();
            arr.add(email);
            storico.writeObject(arr);
            storico.flush();
            storico.close();
            fileOutputStream.close();

        }
    }


    private ArrayList<EmailComplete> getAllInboxEmails() throws IOException, ClassNotFoundException {
        String userFilePath = "src/main/resources/email/inbox_" + utente + ".txt";
        File userFile = new File(userFilePath);
        if(userFile.exists() && !userFile.isDirectory()){
            FileInputStream in = new FileInputStream(userFile);
            ObjectInputStream inObjs = new ObjectInputStream(in);
            ArrayList<EmailComplete> inboxEmails = (ArrayList<EmailComplete>)inObjs.readObject();
            in.close();
            return inboxEmails;
        }
        else return new ArrayList<>();
    }


    private ArrayList<EmailComplete> getAllSentEmails() throws IOException, ClassNotFoundException {
        String userFilePath = "src/main/resources/email/sent_" + utente + ".txt";
        File userFile = new File(userFilePath);
        if(userFile.exists() && !userFile.isDirectory()){
            FileInputStream in = new FileInputStream(userFile);
            ObjectInputStream inObjs = new ObjectInputStream(in);
            ArrayList<EmailComplete> sentEmails = (ArrayList<EmailComplete>)inObjs.readObject();
            in.close();
            return sentEmails;
        }
        else return new ArrayList<>();
    }
}