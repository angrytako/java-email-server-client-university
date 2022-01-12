package com.example.email.model;

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

    public void addEmail(EmailComplete email){
        emails.add(email);
    }


}
