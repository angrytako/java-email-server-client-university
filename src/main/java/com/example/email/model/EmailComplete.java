package com.example.email.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EmailComplete implements Serializable {
    private Integer ID;
    private String mittente;
    private String destinatari;
    private String oggetto;
    private String testo;
    private LocalDateTime data;


    public EmailComplete(Email emailSmall, Integer ID,
                         String mittente, LocalDateTime data) {
        this.oggetto = emailSmall.getOggetto();
        this.destinatari = emailSmall.getDestinatari();
        this.testo= emailSmall.getTesto();
        this.ID = ID;
        this.mittente = mittente;
        this.data = data;
    }
    public Integer getID() {
        return ID;
    }
    public String getMittente() {
        return mittente;
    }

    public String getDestinatari() {
        return destinatari;
    }

    @Override
    public String toString() {
        return "EmailComplete{" +
                "ID=" + ID +
                ", mittente='" + mittente + '\'' +
                ", destinatari='" + destinatari + '\'' +
                ", oggetto='" + oggetto + '\'' +
                ", testo='" + testo + '\'' +
                ", data=" + data +
                '}';
    }
}
