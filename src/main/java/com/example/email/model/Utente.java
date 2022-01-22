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
        this.emails = new ArrayList<EmailComplete>();
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


}
