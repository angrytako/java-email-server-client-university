package com.example.email.model;

import java.util.ArrayList;
import java.util.List;

public class Utente {
    private String nome;  //es@gmail.com
    private List emails;

    public Utente(String nome) {
        this.nome = nome;
        this.emails = new ArrayList<EmailComplete>();
    }

    public String getNome() {
        return nome;
    }
}
