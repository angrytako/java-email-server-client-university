package com.example.email.client;

import com.example.email.Client;
import com.example.email.model.Email;
import com.example.email.model.EmailComplete;
import com.example.email.model.Utente;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
<<<<<<< Updated upstream
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
=======
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
>>>>>>> Stashed changes
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    private final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private final FXMLLoader postaRicevuta = new FXMLLoader(Client.class.getResource("posta-view.fxml"));
    private enum statesEnum { INVIO, POSTA_RICEVUTA, POSTA_IN_USCITA };
    private File file = new File("src/main/resources/img/delate.png");
    private Image image = new Image(file.toURI().toString());
    private statesEnum state;
    private SplitPane postaSp;
    private Email email;
    private Utente utente;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;


    @FXML
    TextField oggettoTF;
    @FXML
    TextField destinatariTF;
    @FXML
    TextArea testoTA;
    @FXML
    AnchorPane masterAp, invioAp;
    @FXML
    Label nomeUtente;





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        utente = new Utente("t@gmail.com");  //questa può essere cambiata, quando l'ho messo non mi era chiaro come lanciare i client
        nomeUtente.setText(utente.getEmailAddress());
        /*il controller crea la connessione con il Server, successivamente due thread di occupano di lettura e scrittura*/

        socket = startServerConnession("localhost", 6868);



        try {
<<<<<<< Updated upstream
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
=======
            postaSp = postaRicevuta.load();
            ListView lv =  ((ListView) postaSp.getItems().get(0));
            AnchorPane inspectedEmail =  ((AnchorPane) postaSp.getItems().get(1));
            lv.itemsProperty().bindBidirectional(utente.inbox);
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
                    ((ImageView)inspectedEmail.lookup("#delate")).setImage(image);
                }
            });
        }catch (IOException err){
            System.out.println(err.toString());
            System.exit(-1);
>>>>>>> Stashed changes
        }
        InputServer inputServer = new InputServer(utente,socket,inputStream,outputStream);
        inputServer.start();

        try {
<<<<<<< Updated upstream
            postaSp = postaRicevuta.load();
=======
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
                    ((ImageView)inspectedEmail.lookup("#delate")).setImage(image);
                }
            });
>>>>>>> Stashed changes
        }catch (IOException err){
            System.out.println(err.toString());
            System.exit(-1);
        }
        state = statesEnum.INVIO;
        this.email = new Email( oggettoTF.textProperty(), destinatariTF.textProperty(), testoTA.textProperty());
        setEmailToSend("Università", "enrico@gmail.com","Enrico è il più figo");
<<<<<<< Updated upstream
=======


>>>>>>> Stashed changes
    }







    private void setEmailToSend(String oggetto, String destinatari, String testo){
        this.oggettoTF.setText(oggetto);
        this.destinatariTF.setText(destinatari);
        this.testoTA.setText(testo);
    }

    public void elimina(){
        System.out.println("uffa");
    }

    public void invia(ActionEvent e){
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


        EmailComplete emailToServer = new EmailComplete(email,null,utente.getEmailAddress(), LocalDateTime.now());
        /*questo da thread può diventare collable così si possono gestire le eccezioni*/
        OutToServer invio = new OutToServer(emailToServer,socket,outputStream);
        invio.start();
        utente.addEmail(emailToServer);
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
                state = statesEnum.POSTA_RICEVUTA;
                System.exit(-1);
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
                state = statesEnum.INVIO;
                System.exit(-1);
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


    public Socket startServerConnession(String host ,int port ) {
        Socket so;
        try {
            so = new Socket(host, port);
            System.out.println(so);
            return so;
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
            return null;
        }
    }

}