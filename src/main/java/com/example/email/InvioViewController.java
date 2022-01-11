package com.example.email;

import com.example.email.Client;
import com.example.email.model.Email;
import com.example.email.model.Utente;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InvioViewController implements Initializable {
    private final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private final FXMLLoader postaRicevuta = new FXMLLoader(Client.class.getResource("posta-view.fxml"));
    private enum statesEnum { INVIO, POSTA_RICEVUTA, POSTA_IN_USCITA };
    private statesEnum state;
    private SplitPane postaSp;
    private Email email;

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
        Utente utente = new Utente("Enrico");  //questa può essere cambiata, quando l'ho messo non mi era chiaro come lanciare i client

        nomeUtente.setText(utente.getNome());
        try {
            postaSp = postaRicevuta.load();
        }catch (IOException err){
            System.out.println(err.toString());
            System.exit(-1);
        }
        state = statesEnum.INVIO;
        this.email = new Email( oggettoTF.textProperty(), destinatariTF.textProperty(), testoTA.textProperty());
        setEmailToSend("Heheheheh", "PaperonDePaperoni@gmail.com","Lorem ipsum bla bla bla");
    }


    private void setEmailToSend(String oggetto, String destinatari, String testo){
        this.oggettoTF.setText(oggetto);
        this.destinatariTF.setText(destinatari);
        this.testoTA.setText(testo);
    }

    public void invia(ActionEvent e){
        System.out.println(email.getOggetto() + " , \n"+ email.getDestinatari() + " , \n" + email.getTesto());
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

}