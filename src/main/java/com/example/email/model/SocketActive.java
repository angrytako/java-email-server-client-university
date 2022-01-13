package com.example.email.model;

import java.util.ArrayList;
import java.util.List;

public class SocketActive {
    private String utente;
    private Thread thread;
    private List<EmailComplete> emails;

    public SocketActive() {
        this.thread =null;
        utente=null;
        emails=new ArrayList<EmailComplete>();
    }

    public synchronized void setUtente(String utente) {
        this.utente = utente;
    }

    public synchronized void setThread(Thread thread) {
        this.thread = thread;
    }

    public synchronized EmailComplete popEmail(){
        EmailComplete out = emails.get(0);
        emails.remove(0);
        return out;
    }
    public synchronized int emailSize(){
        return emails.size();
    }

    public synchronized String getUtente() {
        return utente;
    }

    public synchronized Thread getThread() {
        return thread;
    }

    public synchronized void addEmail(EmailComplete email){
        emails.add(email);
    }

    @Override
    public synchronized String toString() {
        return "SocketActive{" +
                "utente='" + utente + '\'' +
                ", thread=" + thread +
                ", emails=" + emails.toString() +
                '}';
    }
}
