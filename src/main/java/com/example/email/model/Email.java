package com.example.email.model;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

public class Email implements Serializable {
    private SimpleStringProperty oggetto;
    private SimpleStringProperty destinatari;
    private SimpleStringProperty testo;

    public Email(String oggetto, String destinatari, String testo) {
        this.oggetto = new SimpleStringProperty();
        this.destinatari = new SimpleStringProperty();
        this.testo = new SimpleStringProperty();
        this.oggetto.set(oggetto);
        this.destinatari.set(destinatari);
        this.testo.set(testo);
    }
    public Email(Property<String> oggetto, Property<String> destinatari, Property<String> testo){
        this.oggetto = new SimpleStringProperty();
        this.destinatari = new SimpleStringProperty();
        this.testo = new SimpleStringProperty();
        this.oggetto.bindBidirectional(oggetto);
        this.destinatari.bindBidirectional(destinatari);
        this.testo.bindBidirectional(testo);
    }

    public String getOggetto() {
        return oggetto.get();
    }


    public String getDestinatari() {
        return destinatari.get();
    }


    public String getTesto() {
        return testo.get();
    }


    public void reset() {
        this.destinatari.set("");
        this.oggetto.set("");
        this.testo.set("");
    }


}
