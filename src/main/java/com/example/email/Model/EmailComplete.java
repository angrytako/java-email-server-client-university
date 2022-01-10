package com.example.email.Model;

import java.util.Date;

public class EmailComplete extends Email{
    private int ID;
    private String mittente;
    private Date data;
    public EmailComplete(String oggetto, String destinatari, String testo) {
        super(oggetto, destinatari, testo);

    }
}
