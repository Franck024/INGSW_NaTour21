package com.example.natour21.entities;

public class Segnalazione {

    private String authorId;
    private long itinerarioId;
    private String titolo;
    private String descrizione;

    public Segnalazione(){}

    public Segnalazione(String authorId, long itinerarioId, String titolo) {
        this.authorId = authorId;
        this.itinerarioId = itinerarioId;
        this.titolo = titolo;
    }

    public Segnalazione(String authorId, long itinerarioId, String titolo, String descrizione) {
        this.authorId = authorId;
        this.itinerarioId = itinerarioId;
        this.titolo = titolo;
        this.descrizione = descrizione;
    }

    public String getAuthorId() {
        return authorId;
    }

    public long getItinerarioId() {
        return itinerarioId;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}

