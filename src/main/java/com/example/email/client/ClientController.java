package com.example.email.client;

import com.example.email.Client;
import com.example.email.model.Email;
import com.example.email.model.EmailComplete;
import com.example.email.model.Utente;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientController implements Initializable {
    private final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private final FXMLLoader postaRicevuta = new FXMLLoader(Client.class.getResource("posta-view.fxml"));
    private final FXMLLoader postaInviata = new FXMLLoader(Client.class.getResource("postaInviata-view.fxml"));
    private enum statesEnum { INVIO, POSTA_RICEVUTA, POSTA_IN_USCITA };
    private statesEnum state;
    private SplitPane postaSp;
    private SplitPane postaInv;
    private Email email;
    protected Utente utente;


    @FXML
    TextField oggettoTF;
    @FXML
    TextField destinatariTF;
    @FXML
    TextArea testoTA;
    @FXML
    AnchorPane masterAp, invioAp;
    @FXML
    Label nomeUtente,warning,warningInvio,messaaggioTF;
    @FXML
    Button inviaBtn;
    @FXML
    ButtonBar navBtnBar;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        utente = new Utente(loginUtente());  //questa può essere cambiata, quando l'ho messo non mi era chiaro come lanciare i client
        nomeUtente.setText(utente.getEmailAddress());
        /*il controller crea la connessione con il Server, successivamente due thread di occupano di lettura e scrittura*/

        Socket socket = startServerConnection("localhost", 6868);
        if (socket==null){
            warning.setVisible(true);
            CheckServer checkServer = new CheckServer(warning.visibleProperty(),warningInvio.visibleProperty(),false,this);
            checkServer.start();
        }else{
            getEmail(socket);
            CheckServer checkServer = new CheckServer(warning.visibleProperty(),warningInvio.visibleProperty(),true,this);
            checkServer.start();
        }



        try {
            postaSp = postaRicevuta.load();
            ListView lv =  ((ListView) postaSp.getItems().get(0));
            AnchorPane inspectedEmail =  ((AnchorPane) postaSp.getItems().get(1));
            lv.itemsProperty().bindBidirectional(utente.inboxProperty());
            ((Button)inspectedEmail.lookup("#delete")).setOnAction( new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent mouseEvent) {
                    if(lv.getSelectionModel().getSelectedItem()!=null)
                    {
                        Thread deleteThread = new Thread(new DeleteMail( warning.visibleProperty(),
                                (EmailComplete) lv.getSelectionModel().getSelectedItem(),utente));
                        Object lastChoise = lv.getSelectionModel().getSelectedItem();
                        lv.getSelectionModel().selectLast();
                        EmailComplete selectedEmail = ((EmailComplete) lv.getSelectionModel().getSelectedItem());
                        if(lv.getSelectionModel().getSelectedIndex()==0 || selectedEmail == null ||
                                lv.getSelectionModel().getSelectedIndex()==-1 || lv.getSelectionModel().getSelectedItem().equals(lastChoise)){
                            ((Label) inspectedEmail.lookup("#mittenteLb")).setText("");
                            ((Label) inspectedEmail.lookup("#oggettoLb")).setText("");
                            ((Label) inspectedEmail.lookup("#destinatariLb")).setText("");
                            ((Label) inspectedEmail.lookup("#dataLb")).setText("");
                            ((TextArea) inspectedEmail.lookup("#bodyTA")).setText("");
                            ((Label) inspectedEmail.lookup("#idLb")).setText("ID: ");
                        }
                        else {
                            ((Label) inspectedEmail.lookup("#mittenteLb")).setText(selectedEmail.getMittente());
                            ((Label) inspectedEmail.lookup("#oggettoLb")).setText(selectedEmail.getOggetto());
                            ((Label) inspectedEmail.lookup("#destinatariLb")).setText(selectedEmail.getDestinatari());
                            ((Label) inspectedEmail.lookup("#dataLb")).setText(selectedEmail.getData().toString());
                            ((TextArea) inspectedEmail.lookup("#bodyTA")).setText(selectedEmail.getTesto());
                            ((Label) inspectedEmail.lookup("#idLb")).setText("ID: " + selectedEmail.getID());
                        }
//                        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
//                            @Override
//                            public void uncaughtException(Thread th, Throwable ex) {
//                            }
//                        };
                   //     deleteThread.setUncaughtExceptionHandler(h);
                        deleteThread.start();
                    }
                }
            });

            //answer, answerAll, forward btns
            ((Button)inspectedEmail.lookup("#answBtn")).setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    EmailComplete selectedEmail = ((EmailComplete) lv.getSelectionModel().getSelectedItem());
                    if(selectedEmail != null){
                        String subject = ((Label)inspectedEmail.lookup("#oggettoLb")).getText().toString();
                        String sender = ((Label)inspectedEmail.lookup("#mittenteLb")).getText().toString();

                        String body =("\n" +
                        ((TextArea)inspectedEmail.lookup("#bodyTA")).getText().toString()+ "\n"+
                                "Utente: "+sender.toString() + "\n"+
                                "Data: "+ ((Label)inspectedEmail.lookup("#dataLb")).getText().toString()+
                                "\n------------------------------------------------\n"
                        );

                        setEmailToSend(subject,sender,body);

                        messaaggioTF.setText("Rispondi");
                        oggettoTF.setEditable(false);
                        destinatariTF.setEditable(false);
                        testoTA.setEditable(true);
                        spedizioneSwitch();
                        inviaBtn.requestFocus();
                    }
                }
            });
            ((Button)inspectedEmail.lookup("#answAllBtn")).setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    EmailComplete selectedEmail = ((EmailComplete) lv.getSelectionModel().getSelectedItem());
                    if(selectedEmail != null){
                        String subject = ((Label)inspectedEmail.lookup("#oggettoLb")).getText().toString();
                        String sender = ((Label)inspectedEmail.lookup("#mittenteLb")).getText().toString();
                        String body =("\n"+ ((TextArea)inspectedEmail.lookup("#bodyTA")).getText().toString() +
                                "Utente: "+sender.toString() + "\n"+
                                "Data: "+ ((Label)inspectedEmail.lookup("#dataLb")).getText().toString()+ "\n"+
                                "------------------------------------------------\n"
                        );
                        String receivers =  ((Label)inspectedEmail.lookup("#destinatariLb")).getText().toString();
                        String destination = removeSelfAndConcat(sender,receivers);
                        setEmailToSend(subject,destination,body);

                        messaaggioTF.setText("Rispondi a tutti");
                        oggettoTF.setEditable(false);
                        destinatariTF.setEditable(false);
                        testoTA.setEditable(true);
                        spedizioneSwitch();
                        inviaBtn.requestFocus();
                    }
                }
            });
            ((Button)inspectedEmail.lookup("#forwardInBtn")).setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    EmailComplete selectedEmail = ((EmailComplete) lv.getSelectionModel().getSelectedItem());
                    if(selectedEmail != null){
                        String subject = ((Label)inspectedEmail.lookup("#oggettoLb")).getText().toString();
                        String body =("Utente: "+((Label)inspectedEmail.lookup("#mittenteLb")).getText().toString() + "\n"+
                                "Data: "+ ((Label)inspectedEmail.lookup("#dataLb")).getText().toString()+ "\n"+
                                "Testo:\n"+
                                ((TextArea)inspectedEmail.lookup("#bodyTA")).getText().toString() +
                                "\n------------------------------------------------\n"
                        );
                        setEmailToSend(subject,"",body);

                        messaaggioTF.setText("Inoltra");
                        oggettoTF.setEditable(false);
                        testoTA.setEditable(false);
                        destinatariTF.setEditable(true);
                        spedizioneSwitch();
                        inviaBtn.requestFocus();
                    }
                }
            });
            lv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    EmailComplete selectedEmail = ((EmailComplete) lv.getSelectionModel().getSelectedItem());
                    if(selectedEmail != null) {
                        ((Label) inspectedEmail.lookup("#mittenteLb")).setText(selectedEmail.getMittente());
                        ((Label) inspectedEmail.lookup("#oggettoLb")).setText(selectedEmail.getOggetto());
                        ((Label) inspectedEmail.lookup("#destinatariLb")).setText(selectedEmail.getDestinatari());
                        ((Label) inspectedEmail.lookup("#dataLb")).setText(selectedEmail.getData().toString());
                        ((TextArea) inspectedEmail.lookup("#bodyTA")).setText(selectedEmail.getTesto());
                        ((Label) inspectedEmail.lookup("#idLb")).setText("ID: " + selectedEmail.getID());
                    }


                }
            });
        }catch (IOException err){
            System.out.println(err.toString());
            System.exit(-1);
        }



        try {
            postaInv = postaInviata.load();
            ListView lv =  ((ListView) postaInv.getItems().get(0));
            AnchorPane inspectedEmail =  ((AnchorPane) postaInv.getItems().get(1));

            lv.itemsProperty().bindBidirectional(utente.sentEmailsProperty());
            ((Button)inspectedEmail.lookup("#delete")).setOnAction( new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent mouseEvent) {
                    if(lv.getSelectionModel().getSelectedItem()!=null){
                        DeleteMail deleteThread = new DeleteMail( warning.visibleProperty(),
                                (EmailComplete) lv.getSelectionModel().getSelectedItem(),utente);

                        EmailComplete selectedEmail = ((EmailComplete) lv.getSelectionModel().getSelectedItem());
                        lv.getSelectionModel().selectLast();
                        if(lv.getSelectionModel().getSelectedIndex()==0 || selectedEmail == null ||
                                lv.getSelectionModel().getSelectedItem().equals(selectedEmail) ){
                            ((Label) inspectedEmail.lookup("#oggettoLb")).setText("");
                            ((Label) inspectedEmail.lookup("#destinatariLb")).setText("");
                            ((Label) inspectedEmail.lookup("#dataLb")).setText("");
                            ((TextArea) inspectedEmail.lookup("#bodyTA")).setText("");
                            ((Label) inspectedEmail.lookup("#idLb")).setText("ID: ");
                        }else{
                            ((Label) inspectedEmail.lookup("#oggettoLb")).setText(selectedEmail.getOggetto());
                            ((Label) inspectedEmail.lookup("#destinatariLb")).setText(selectedEmail.getDestinatari());
                            ((Label) inspectedEmail.lookup("#dataLb")).setText(selectedEmail.getData().toString());
                            ((TextArea) inspectedEmail.lookup("#bodyTA")).setText(selectedEmail.getTesto());
                            ((Label) inspectedEmail.lookup("#idLb")).setText("ID: " + selectedEmail.getID());
                        }
//                        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
//                            @Override
//                            public void uncaughtException(Thread th, Throwable ex) {
//                            }
//                        };
//                        deleteThread.setUncaughtExceptionHandler(h);
                        deleteThread.start();
                    }

                }
            });
            lv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    EmailComplete selectedEmail = ((EmailComplete) lv.getSelectionModel().getSelectedItem());
                    if(selectedEmail != null) {
                        ((Label) inspectedEmail.lookup("#oggettoLb")).setText(selectedEmail.getOggetto());
                        ((Label) inspectedEmail.lookup("#destinatariLb")).setText(selectedEmail.getDestinatari());
                        ((Label) inspectedEmail.lookup("#dataLb")).setText(selectedEmail.getData().toString());
                        ((TextArea) inspectedEmail.lookup("#bodyTA")).setText(selectedEmail.getTesto());
                        ((Label) inspectedEmail.lookup("#idLb")).setText("ID: " + selectedEmail.getID());
                    }
                }
            });

            //forward btn
            ((Button)inspectedEmail.lookup("#forwardOutBtn")).setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    EmailComplete selectedEmail = ((EmailComplete) lv.getSelectionModel().getSelectedItem());
                    if(selectedEmail != null){
                        String subject = ((Label)inspectedEmail.lookup("#oggettoLb")).getText().toString();
                        String body =("Utente: "+ utente.getEmailAddress() + "\n"+
                                "Data: "+ ((Label)inspectedEmail.lookup("#dataLb")).getText().toString()+ "\n"+
                                "Testo:\n"+
                                ((TextArea)inspectedEmail.lookup("#bodyTA")).getText().toString() +
                                "\n------------------------------------------------\n"
                        );

                        setEmailToSend(subject,"",body);
                        messaaggioTF.setText("Inoltra");
                        oggettoTF.setEditable(false);
                        testoTA.setEditable(false);
                        destinatariTF.setEditable(true);
                        spedizioneSwitch();
                        inviaBtn.requestFocus();

                    }
                }
            });
        }catch (IOException err){
            System.out.println(err.toString());
            System.exit(-1);
        }

        state = statesEnum.INVIO;
        this.email = new Email( oggettoTF.textProperty(), destinatariTF.textProperty(), testoTA.textProperty());
        setEmailToSend("Università", "enrico@gmail.com","Enrico è il più figo");



    }


    private void setEmailToSend(String oggetto, String destinatari, String testo){
        this.oggettoTF.setText(oggetto);
        this.destinatariTF.setText(destinatari);
        this.testoTA.setText(testo);
    }

    public void invia(ActionEvent e){
        if(warning.isVisible()){
            warningInvio.setVisible(true);
        }else{
            if(email.getOggetto().length() == 0){
                displayErr(oggettoTF);
                oggettoTF.requestFocus();
                return;
            }
            if(!validateDestinatari(email.getDestinatari())){
                displayErr(destinatariTF);
                destinatariTF.requestFocus();
                return;
            }
            if(email.getTesto().length() == 0){
                displayErr(testoTA);
                testoTA.requestFocus();
                return;
            }


            EmailComplete emailToServer = new EmailComplete(email,null, utente.getEmailAddress(), LocalDateTime.now());

            Socket socket = ClientController.startServerConnection("localhost", 6868);
            if(socket==null){
                warningInvio.setVisible(true);
            }else{
                OutToServer invio = new OutToServer(emailToServer, utente, socket, warningInvio);
                invio.start();
                email.reset();
            }


        }
    }


    public void postaInviataSwitch(ActionEvent e){
        switch (state){
            case INVIO:
            {
                masterAp.getChildren().remove(invioAp);
                masterAp.getChildren().add(postaInv);
                state = statesEnum.POSTA_IN_USCITA;
                break;
            }
            case POSTA_RICEVUTA:
            {
                masterAp.getChildren().remove(postaSp);
                masterAp.getChildren().add(postaInv);
                state = statesEnum.POSTA_IN_USCITA;
                return;
            }
            case POSTA_IN_USCITA:{
               break;
            }
        }

    }

    public void postaRicevutaSwitch(ActionEvent e){
        switch (state){
            case INVIO:
                {
                    masterAp.getChildren().remove(invioAp);
                    masterAp.getChildren().add(postaSp);
                    state = statesEnum.POSTA_RICEVUTA;
                    break;
                }
            case POSTA_RICEVUTA:{
                return;
            }
            case POSTA_IN_USCITA:{
                masterAp.getChildren().remove(postaInv);
                masterAp.getChildren().add(postaSp);
                state = statesEnum.POSTA_RICEVUTA;
                break;
            }
        }

    }

    private void spedizioneSwitch(){
        switch (state){
            case INVIO:
            {
                return;
            }
            case POSTA_RICEVUTA:{
                masterAp.getChildren().remove(postaSp);
                masterAp.getChildren().add(invioAp);
                state = statesEnum.INVIO;

                break;
            }
            case POSTA_IN_USCITA:{
                masterAp.getChildren().remove(postaInv);
                masterAp.getChildren().add(invioAp);
                state = statesEnum.INVIO;
                break;
            }
        }

    }
    private void setUpInviaVista(){
        messaaggioTF.setText("Invia");
        oggettoTF.setEditable(true);
        destinatariTF.setEditable(true);
        testoTA.setEditable(true);
        oggettoTF.setText("");
        destinatariTF.setText("");
        testoTA.setText("");
    }

    public void inviaSwitch(ActionEvent e){
        switch (state){
            case INVIO:
            {
                setUpInviaVista();
                return;
            }
            case POSTA_RICEVUTA:{
                masterAp.getChildren().remove(postaSp);
                masterAp.getChildren().add(invioAp);
                setUpInviaVista();
                state = statesEnum.INVIO;
                break;
            }
            case POSTA_IN_USCITA:{
                masterAp.getChildren().remove(postaInv);
                masterAp.getChildren().add(invioAp);
                setUpInviaVista();
                state = statesEnum.INVIO;
                break;
            }
        }

    }
    private boolean validateDestinatari(String destinatari){
        if(destinatari.length() == 0)
            return false;
        String[] emails = destinatari.split(",");
        for (String email : emails){
            if(!email.trim().toLowerCase().matches(EMAIL_REGEX))
                return false;
        }
        return true;
    }
    private void displayErr(Node n){
        n.setStyle("-fx-border-color: red");
        PauseTransition wait = new PauseTransition(Duration.seconds(1));
        wait.setOnFinished(event -> {
            n.setStyle("-fx-border-style: none");
        });
        wait.play();
    }


    public static Socket startServerConnection(String host , int port ) {
        Socket so;
        try {
            so = new Socket(host, port);
           // System.out.println(so);
            return so;
        } catch (IOException e) {
           // System.out.println(e+" Server non raggiungibile");
            //e.printStackTrace();
            return null;
        }
    }

    protected void getEmail(Socket socket){
        try {
            OutputStream outStream = socket.getOutputStream();
            InputStream inStream = socket.getInputStream();
            ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
            ObjectInputStream objInStream = new ObjectInputStream(inStream);
            objOutStream.writeObject(utente.getEmailAddress());
            System.out.println("about to print");
            objOutStream.writeObject("GET ALL");
            System.out.println("sent");
            System.out.println("waiting response");
            String answer = (String) objInStream.readObject();
            System.out.println(answer);
            if(answer.equals("OK")){
                try {
                    System.out.println("waiting the lists");
                    ArrayList<EmailComplete> inbox = (ArrayList<EmailComplete>) objInStream.readObject();
                    ArrayList<EmailComplete> sent = (ArrayList<EmailComplete>) objInStream.readObject();
                    utente.addEmailSent(sent);
                    utente.addEmailInbox(inbox);


                    System.out.println(inbox.toString());
                    System.out.println(sent.toString());
                }catch (Exception e){
                    System.out.println("Error to handle");
                    System.out.println(e);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private String loginUtente(){
        //login leggo l'utente da file
        int u=0;
        try {
            FileReader fileIn = new FileReader("src/main/resources/utente.txt");
            u=fileIn.read();
            if (u==-1) System.out.println("errore lettura file utente");
            else System.out.println(u-48);
            fileIn.close();
            FileWriter fileOut = new FileWriter("src/main/resources/utente.txt");
            u=(((u-48)+1)%3)+48;
            fileOut.write(u);
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String emailUtente="t@gmail.com";
        switch (u-48){
            case 0:{
                emailUtente = "enrico@gmail.com";
                break;
            }
            case 1:{
                emailUtente = "lorenzo@gmail.com";
                break;
            }
            case 2:{
                emailUtente = "anna@libero.it";
                break;
            }
        }
        return emailUtente;
    }
    private String removeSelfAndConcat(String sender, String receivers){
        String[] receiversArr = receivers.split(",");
        StringBuffer result = new StringBuffer();
        result.append(sender);
        for (String receiver : receiversArr){
            if(!receiver.trim().equals(utente.getEmailAddress())){
                result.append(", " + receiver);
            }
        }
        return result.toString();
    }

}