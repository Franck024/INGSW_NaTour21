package com.example.natour21.entities;

import com.example.natour21.enums.DifficoltaItinerario;

public class Segnalazione {

    private String authorId;
    private long itinerarioId;
    private String titolo;
    private String descrizione;
    private int durata;
    private DifficoltaItinerario difficolta;

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

    public int getDurata() { return durata; }

    public void setDurata(int durata) { this.durata = durata; }

    public DifficoltaItinerario getDifficolta() { return difficolta; }

    public void setDifficolta(DifficoltaItinerario difficolta) { this.difficolta = difficolta; }
}

