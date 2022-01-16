package com.example.email.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EmailComplete implements Serializable {

    private String ID;
    private String mittente;
    private String destinatari;
    private String oggetto;
    private String testo;
    private LocalDateTime data;


    public EmailComplete(Email emailSmall, String ID,
                         String mittente, LocalDateTime data) {
        this.oggetto = emailSmall.getOggetto();
        this.destinatari = emailSmall.getDestinatari();
        this.testo= emailSmall.getTesto();
        this.ID = ID;
        this.mittente = mittente;
        this.data = data;
    }
    public EmailComplete(Email emailSmall,
                         String mittente, LocalDateTime data) {
        this.oggetto = emailSmall.getOggetto();
        this.destinatari = emailSmall.getDestinatari();
        this.testo= emailSmall.getTesto();
        this.mittente = mittente;
        this.data = data;
    }
    public String getID() {
        return ID;
    }
    public String getOggetto() {
        return oggetto;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getTesto() {
        return testo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
    public String getMittente() {
        return mittente;
    }

    public String getDestinatari() {
        return destinatari;
    }

    @Override
    public String toString() {
        return oggetto;
    }
}
