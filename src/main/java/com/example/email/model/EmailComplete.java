package com.example.email.model;

import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

public class EmailComplete extends Email{
    private IntegerPropertyBase ID;
    private SimpleStringProperty mittente;
    private Property<Date> data;

    public EmailComplete(Email emailSmall, IntegerPropertyBase ID,
                         SimpleStringProperty mittente, Property<Date> data) {
        super(emailSmall.getOggetto(), emailSmall.getDestinatari(), emailSmall.getTesto());
        this.ID = ID;
        this.mittente = mittente;
        this.data = data;
    }


}
