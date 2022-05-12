package com.example.natour21;

import com.example.natour21.enums.DifficoltaItinerario;

public class ParentItem {

    private long idItinerario;
    private String nomeItinerario;
    private String difficolta;
    private String durata;
    private String nomePuntoIniziale;
    private String autore;

    public ParentItem(long idItinerario, String nomeItinerario, DifficoltaItinerario difficolta, int durata,
                      String nomePuntoIniziale, String autore) {
        this.idItinerario = idItinerario;
        this.nomeItinerario = nomeItinerario;
        this.difficolta = difficolta.toString();
        this.durata = intMinutesToHourAndMinutesString(durata);
        this.nomePuntoIniziale = nomePuntoIniziale;
        this.autore = autore;
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
}
