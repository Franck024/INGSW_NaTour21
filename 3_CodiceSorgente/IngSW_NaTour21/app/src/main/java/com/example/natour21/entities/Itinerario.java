package com.example.natour21.entities;

import com.example.natour21.enums.DifficoltaItinerario;
public class Itinerario {

    private long id;
    private String authorId;
    private String nome;
    private String nomePuntoIniziale;
    private int durata;
    private DifficoltaItinerario difficoltaItinerario;
    String descrizione;
    private String tracciatoKey;

    public Itinerario(){}

    public Itinerario(long id, String authorId, String nome, String nomePuntoIniziale, int durata, DifficoltaItinerario difficoltaItinerario, String tracciatoKey) {
        this.id = id;
        this.authorId = authorId;
        this.nome = nome;
        this.nomePuntoIniziale = nomePuntoIniziale;
        this.durata = durata;
        this.difficoltaItinerario = difficoltaItinerario;
        this.tracciatoKey = tracciatoKey;
    }

    public Itinerario(long id, String authorId, String nome, String nomePuntoIniziale, int durata, DifficoltaItinerario difficoltaItinerario, String descrizione, String tracciatoKey) {
        this.id = id;
        this.authorId = authorId;
        this.nome = nome;
        this.nomePuntoIniziale = nomePuntoIniziale;
        this.durata = durata;
        this.difficoltaItinerario = difficoltaItinerario;
        this.descrizione = descrizione;
        this.tracciatoKey = tracciatoKey;
    }

    public long getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getNome() {
        return nome;
    }

    public String getNomePuntoIniziale() {
        return nomePuntoIniziale;
    }

    public int getDurata() {
        return durata;
    }

    public DifficoltaItinerario getDifficoltaItinerario() {
        return difficoltaItinerario;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTracciatoKey() {
        return tracciatoKey;
    }
}