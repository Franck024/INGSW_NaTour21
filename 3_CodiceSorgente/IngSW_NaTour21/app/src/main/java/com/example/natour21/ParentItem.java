package com.example.natour21;

import com.example.natour21.enums.DifficoltaItinerario;

public class ParentItem {

    private String nomePercorso;
    private String difficolta;
    private String durata;
    private String nomePuntoIniziale;
    private String autore;

    public ParentItem(String nomePercorso, DifficoltaItinerario difficolta, int durata,
                      String nomePuntoIniziale, String autore) {
        this.nomePercorso = nomePercorso;
        this.difficolta = difficolta.toString();
        this.durata = intMinutesToHourAndMinutesString(durata);
        this.nomePuntoIniziale = nomePuntoIniziale;
        this.autore = autore;
    }

    private String intMinutesToHourAndMinutesString(int minutes){
        return (minutes/60 + "h " + minutes%60 + "m");
    }

    public String getNomePercorso() {
        return nomePercorso;
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
