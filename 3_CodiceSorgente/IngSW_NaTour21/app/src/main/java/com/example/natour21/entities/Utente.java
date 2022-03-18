package com.example.natour21.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Utente {

    private String email;
    private String nome;
    private String cognome;
    private String cellulare;
    private String citta;
    @JsonProperty("isAdmin")
    private boolean isAdmin;

    public Utente(){}

    public Utente(String email, String nome, String cognome, boolean isAdmin){
        this.email = email;
        this.nome = nome;
        this.cognome = cognome;
        this.isAdmin = isAdmin;
    }

    public Utente(String email, String nome, String cognome, String cellulare, String citta,
                  boolean isAdmin){
        this.email = email;
        this.nome = nome;
        this.cognome = cognome;
        this.isAdmin = isAdmin;
        this.cellulare = cellulare;
        this.citta = citta;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCellulare() {
        return cellulare;
    }

    public void setCellulare(String cellulare) {
        this.cellulare = cellulare;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
