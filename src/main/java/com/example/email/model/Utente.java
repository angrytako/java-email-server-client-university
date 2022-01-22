package com.example.email.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Utente {
    private String emailAddress;  //es@gmail.com
    private List<EmailComplete> emails;
    public SimpleListProperty<EmailComplete> inbox;
    public SimpleListProperty<EmailComplete> sentEmails;

    public Utente(String nome) {
        this.emailAddress = nome;
        this.inbox = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.sentEmails = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public synchronized String getEmailAddress() {
        return emailAddress;
    }

    public synchronized void addEmail(EmailComplete email){
        emails.add(email);
    }

    public synchronized LocalDateTime getLocalDateTimeLastEmailInbox(){
        if (inbox.size()==0) return null;
        return inbox.get(inbox.size()-1).getData();
    }

    public synchronized void deleteEmail(EmailComplete emailToDelete){

        if(emailToDelete.getMittente().equals(emailAddress)){
            for (EmailComplete email:sentEmails) {
                if (email.getID().equals(emailToDelete.getID())) {
                    sentEmails.remove(email);
                }
            }
            String[] receivers = emailToDelete.getDestinatari().split(",");
            for(String receiver : receivers){
                if(receiver.equals(emailAddress)) {
                    for (EmailComplete email:inbox) {
                        if (email.getID().equals(emailToDelete.getID())) {
                            inbox.remove(email);
                        }
                    }
                }
            }
        }
        else{
            for (EmailComplete email:inbox) {
                if (email.getID().equals(emailToDelete.getID())) {
                    inbox.remove(email);
                }
            }
        }


    }



    }
