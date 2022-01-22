package com.example.email.model;

import javafx.beans.property.Property;

import java.util.ArrayList;
import java.util.List;

public class Utente {
    private String emailAddress;  //es@gmail.com
    private List<EmailComplete> emails;

    public Utente(String nome) {
        this.emailAddress = nome;
        this.emails = new ArrayList<EmailComplete>();
    }

    public synchronized String getEmailAddress() {
        return emailAddress;
    }

    public synchronized void addEmail(EmailComplete email){
        emails.add(email);
    }

<<<<<<< Updated upstream
=======
    public synchronized LocalDateTime getLocalDateTimeLastEmailInbox(){
        if (inbox.size()>0) return null;
        return inbox.get(inbox.size()-1).getData();
    }

>>>>>>> Stashed changes

}
