package com.example.email.client;

import com.example.email.model.EmailComplete;
import com.example.email.model.Utente;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;


public class OutToServer extends Thread{
    private Socket socket;
    private EmailComplete email;
    private Utente user;
    private Label warningLabel;
    public OutToServer(EmailComplete email, Utente user,Socket socket, Label warningLabel) {
        this.email = email;
        this.user = user;
        this.socket=socket;
        this.warningLabel = warningLabel;
    }


    @Override
    public void run() {
        try {
            OutputStream outStream = socket.getOutputStream();
            ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
            InputStream inStream = socket.getInputStream();
            ObjectInputStream objInStream = new ObjectInputStream(inStream);
            objOutStream.writeObject(user.getEmailAddress());
            objOutStream.writeObject("SEND");
            objOutStream.flush();
            String answer = (String) objInStream.readObject();
            if(answer.equals("OK")){
                objOutStream.writeObject(email);
                objOutStream.flush();
            }else {
                final String answerSup = answer;
                Platform.runLater(() -> {
                    String previousText = warningLabel.getText();
                    warningLabel.setText(answerSup);
                    PauseTransition wait = new PauseTransition(Duration.seconds(1));
                    wait.setOnFinished(event -> {
                        warningLabel.setText(previousText);
                    });
                    wait.play();
                });
                return;
            }
            answer = (String) objInStream.readObject();
            if(answer.equals("OK")) {
                EmailComplete sentEmail = (EmailComplete) objInStream.readObject();
                System.out.println(sentEmail.toString());
                Platform.runLater(() -> {user.sentEmailsProperty().add(sentEmail);});
                if(sentEmail.getDestinatari().contains(user.getEmailAddress()))
                    Platform.runLater(() -> {user.inboxProperty().add(sentEmail);});
            }else{
                final String answerSup = answer;
                Platform.runLater(() -> {
                    String previousText = warningLabel.getText();
                    warningLabel.setText(answerSup);
                    PauseTransition wait = new PauseTransition(Duration.seconds(1));
                    wait.setOnFinished(event -> {
                        warningLabel.setText(previousText);
                    });
                    wait.play();
                });
                return;
            }

            } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
