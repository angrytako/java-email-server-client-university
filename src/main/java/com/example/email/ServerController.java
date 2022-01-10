package com.example.email;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {


    @FXML
    TextArea log;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.appendText("-Server is starting...\n");
        ServerListener server = new ServerListener(log,6868);  //6868 is the port
        server.start();
        log.appendText("-Server ON");
    }
}
