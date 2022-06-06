package com.example.natour21;

import com.example.natour21.entities.Itinerario;
import com.example.natour21.enums.DifficoltaItinerario;

public class Post {

    private long idItinerario;
    private String nomeItinerario;
    private String difficolta;
    private String durata;
    private String nomePuntoIniziale;
    private String autore;
    private Double puntoInizialeLat;
    private Double puntoInizialeLong;

    public Post(long idItinerario, String nomeItinerario, DifficoltaItinerario difficolta, int durata,
                String nomePuntoIniziale, Double puntoInizialeLat, Double puntoInizialeLong, String autore) {
        this.idItinerario = idItinerario;
        this.nomeItinerario = nomeItinerario;
        this.difficolta = difficolta.toString();
        this.durata = intMinutesToHourAndMinutesString(durata);
        this.nomePuntoIniziale = nomePuntoIniziale;
        this.puntoInizialeLat = puntoInizialeLat;
        this.puntoInizialeLong = puntoInizialeLong;
        this.autore = autore;
    }

    public Post(Itinerario itinerario, String author){
        this
                (itinerario.getId(),
                        itinerario.getNome(),
                        itinerario.getDifficoltaItinerario(),
                        itinerario.getDurata(),
                        itinerario.getNomePuntoIniziale(),
                        itinerario.getPuntoInizialeLat(),
                        itinerario.getPuntoInizialeLong(),
                        author);
    }


    private String intMinutesToHourAndMinutesString(int minutes){
        if (minutes < 60) return (minutes + "m");
        return (minutes/60 + "h " + minutes%60 + "m");
    }

    public long getIdItinerario(){ return idItinerario;}

    public String getNomeItinerario() {
        return nomeItinerario;
    }

    public String getDifficolta() {
        return difficolta;
    }

    public String getDurata() {
        return durata;
    }

    public String getNomePuntoIniziale() {
        return nomePuntoIniziale;
    }

    public String getAutore() {
        return autore;
    }

    public Double getPuntoInizialeLat() {
        return puntoInizialeLat;
    }

    public void setPuntoInizialeLat(Double puntoInizialeLat) {
        this.puntoInizialeLat = puntoInizialeLat;
    }

    public Double getPuntoInizialeLong() {
        return puntoInizialeLong;
    }

    public void setPuntoInizialeLong(Double puntoInizialeLong) {
        this.puntoInizialeLong = puntoInizialeLong;
    }
}

