package com.example.email.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;

public class Client extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("invio-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MailExp");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        UUID uuid = UUID. randomUUID();
        String uuidAsString = uuid. toString();
        System. out. println("Your UUID is: " + uuidAsString);
      //  launch();
    }
}