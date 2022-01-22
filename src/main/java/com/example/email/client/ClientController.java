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
    Label nomeUtente,warning,warningInvio;





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
            lv.itemsProperty().bindBidirectional(utente.inbox);
            ((Button)inspectedEmail.lookup("#delete")).setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    System.out.println("ciao");
                }
            });
            lv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    EmailComplete selectedEmail = ((EmailComplete) lv.getSelectionModel().getSelectedItem());
                    ((Label)inspectedEmail.lookup("#mittenteLb")).setText(selectedEmail.getMittente());
                    ((Label)inspectedEmail.lookup("#oggettoLb")).setText(selectedEmail.getOggetto());
                    ((Label)inspectedEmail.lookup("#destinatariLb")).setText(selectedEmail.getDestinatari());
                    ((Label)inspectedEmail.lookup("#dataLb")).setText(selectedEmail.getData().toString());
                    ((TextArea)inspectedEmail.lookup("#bodyTA")).setText(selectedEmail.getTesto());
                    ((Label)inspectedEmail.lookup("#idLb")).setText("ID: "+selectedEmail.getID());


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
            lv.itemsProperty().bindBidirectional(utente.sentEmails);
            lv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    EmailComplete selectedEmail = ((EmailComplete) lv.getSelectionModel().getSelectedItem());
                    ((Label)inspectedEmail.lookup("#oggettoLb")).setText(selectedEmail.getOggetto());
                    ((Label)inspectedEmail.lookup("#destinatariLb")).setText(selectedEmail.getDestinatari());
                    ((Label)inspectedEmail.lookup("#dataLb")).setText(selectedEmail.getData().toString());
                    ((TextArea)inspectedEmail.lookup("#bodyTA")).setText(selectedEmail.getTesto());
                    ((Label)inspectedEmail.lookup("#idLb")).setText("ID: "+selectedEmail.getID());
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
            /*questo da thread può diventare collable così si possono gestire le eccezioni*/
            Socket socket = ClientController.startServerConnection("localhost", 6868);
            if(socket==null){
                warningInvio.setVisible(true);
            }else{
                OutToServer invio = new OutToServer(emailToServer, utente, socket);
                invio.start();
                email.reset();
            }


        }
    }


    public void postaInviataSwtich(ActionEvent e){
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

    public void postaRicevutaSwtich(ActionEvent e){
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

    public void inviaSwitch(ActionEvent e){
        switch (state){
            case INVIO:
            {
                return;
            }
            case POSTA_RICEVUTA:{
                state = statesEnum.INVIO;
                masterAp.getChildren().remove(postaSp);
                masterAp.getChildren().add(invioAp);
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
                    utente.sentEmails.addAll(sent);
                    utente.inbox.addAll(inbox);

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

}