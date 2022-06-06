package com.example.natour21.entities;

import com.example.natour21.enums.DifficoltaItinerario;

public class CorrezioneItinerario {

    private String authorId;
    private long itinerarioId;
    private DifficoltaItinerario difficolta;
    private Integer durata;


    private CorrezioneItinerario(){}

    public CorrezioneItinerario(String authorId, long itinerarioId, DifficoltaItinerario difficolta) {
        this.authorId = authorId;
        this.itinerarioId = itinerarioId;
        this.difficolta = difficolta;
    }

    public CorrezioneItinerario(String authorId, long itinerarioId, int durata) {
        this.authorId = authorId;
        this.itinerarioId = itinerarioId;
        this.durata = durata;
    }

    public CorrezioneItinerario(String authorId, long itinerarioId, DifficoltaItinerario difficolta, Integer durata) {
        this.authorId = authorId;
        this.itinerarioId = itinerarioId;
        this.difficolta = difficolta;
        this.durata = durata;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public long getItinerarioId() {
        return itinerarioId;
    }

    public void setItinerarioId(long itinerarioId) {
        this.itinerarioId = itinerarioId;
    }

    public DifficoltaItinerario getDifficolta() {
        return difficolta;
    }

    public void setDifficolta(DifficoltaItinerario difficolta) {
        this.difficolta = difficolta;
    }

    public Integer getDurata() {
        return durata;
    }

    public void setDurata(int durata) {
        this.durata = durata;
    }
}
