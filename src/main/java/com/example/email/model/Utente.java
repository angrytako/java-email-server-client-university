package com.example.email.model;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utente {
    private String emailAddress;  //es@gmail.com
    private SimpleListProperty<EmailComplete> inbox;
    private SimpleListProperty<EmailComplete> sentEmails;

    public Utente(String nome) {
        this.emailAddress = nome;
        this.inbox = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.sentEmails = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public synchronized String getEmailAddress() {
        return emailAddress;
    }


    public SimpleListProperty<EmailComplete> inboxProperty() {
        return inbox;
    }

    public SimpleListProperty<EmailComplete> sentEmailsProperty() {
        return sentEmails;
    }

    public synchronized LocalDateTime getLocalDateTimeLastEmailInbox() {
        if (inbox.size() == 0) return null;
        return inbox.get(inbox.size() - 1).getData();
    }

    public synchronized void addEmailInbox(ArrayList<EmailComplete> inbox) {
        this.inbox.addAll(inbox);
    }

    public synchronized void addEmailSent(ArrayList<EmailComplete> sent) {
        this.sentEmails.addAll(sent);
    }

    public synchronized void deleteEmail(EmailComplete emailToDelete) {

        EmailComplete toDelete = null;

        if (emailToDelete.getMittente().equals(emailAddress)) {
            for (EmailComplete email : sentEmails) {
                if (email.getID().equals(emailToDelete.getID())) {
                    toDelete = email;
                }
            }
            EmailComplete finalToDelete2 = toDelete;
            Platform.runLater(() -> {
                if (finalToDelete2 != null)
                    sentEmails.removeAll(finalToDelete2);
            });

            String[] receivers = emailToDelete.getDestinatari().split(",");
            for (String receiver : receivers) {
                if (receiver.equals(emailAddress)) {
                    toDelete = null;
                    for (EmailComplete email : inbox) {
                        if (email.getID().equals(emailToDelete.getID())) {
                            toDelete = email;
                        }
                    }
                    EmailComplete finalToDelete1 = toDelete;
                    Platform.runLater(() -> {
                            if (finalToDelete1 != null)
                                inbox.removeAll(finalToDelete1);
                        });


                }
            }
        } else {
            for (EmailComplete email : inbox) {
                if (email.getID().equals(emailToDelete.getID())) {
                    toDelete = email;
                }
            }
            EmailComplete finalToDelete = toDelete;
            Platform.runLater(() -> {
                if (finalToDelete != null)
                    inbox.removeAll(finalToDelete);
            });
        }


    }


}
