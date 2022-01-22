package com.example.email.client;

import com.example.email.Client;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmailController implements Initializable {
    private final FXMLLoader postaRicevuta = new FXMLLoader(Client.class.getResource("posta-view.fxml"));
    private final FXMLLoader postaInviata = new FXMLLoader(Client.class.getResource("postaInviata-view.fxml"));
    private SplitPane postaSp;
    private SplitPane postaInv;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            postaSp = postaRicevuta.getController()
            postaInv = postaInviata.load();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void delate(){
        System.out.println(postaInv);
        System.out.println(postaSp);
    }
}
