package com.example.email.server;

import com.example.email.model.EmailComplete;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
<<<<<<< Updated upstream
=======
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
                else log.appendText("\n"+utente+": CHECK");
                outObjStream.writeObject(newMails);
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
>>>>>>> Stashed changes




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
                String userFilePath = "src/main/resources/email/sent_"+utente+".txt";
                File userFile = new File(userFilePath);
                if(userFile.exists() && !userFile.isDirectory()){
                    FileInputStream in = new FileInputStream(userFile);
                    ObjectInputStream inObjs = new ObjectInputStream(in);
                    ArrayList<EmailComplete> availableEmails = (ArrayList<EmailComplete>)inObjs.readObject();
                    availableEmails.add(email);
                    FileOutputStream fileOutputStream = new FileOutputStream(userFilePath);
                    ObjectOutputStream storico = new ObjectOutputStream(fileOutputStream);
                    storico.writeObject(availableEmails);
                    storico.flush();
                    storico.close();
                    fileOutputStream.close();
                }
                else {
                    FileOutputStream fileOutputStream = new FileOutputStream(userFilePath);
                    ObjectOutputStream storico = new ObjectOutputStream(fileOutputStream);
                    ArrayList<EmailComplete> arr = new ArrayList<EmailComplete>();
                    arr.add(email);
                    storico.writeObject(arr);
                    storico.flush();
                    storico.close();
                    fileOutputStream.close();

                }
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