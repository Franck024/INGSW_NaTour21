package com.example.natour21.entities;

import com.example.natour21.enums.DifficoltaItinerario;
public class Itinerario {

    public static String getDescrizione;
    private long id;
    private static String authorId;
    private static String nome;
    private static String nomePuntoIniziale;
    private static int durata;
    private static DifficoltaItinerario difficoltaItinerario;
    static String descrizione;
    private static String tracciatoKey;
    private static int warning;

    public Itinerario(){}

    public Itinerario(long id, String authorId, String nome,
                      String nomePuntoIniziale, int durata, DifficoltaItinerario difficoltaItinerario, int warning) {
        this.id = id;
        this.authorId = authorId;
        this.nome = nome;
        this.nomePuntoIniziale = nomePuntoIniziale;
        this.durata = durata;
        this.difficoltaItinerario = difficoltaItinerario;
        this.warning = warning;
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

    public static String getAuthorId() {
        return authorId;
    }

    public static String getNome() {
        return nome;
    }

    public static String getNomePuntoIniziale() {
        return nomePuntoIniziale;
    }

    public static int getDurata() {
        return durata;
    }

    public static DifficoltaItinerario getDifficoltaItinerario() {
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

    public static int getWarning() {
        return warning;
    }
}