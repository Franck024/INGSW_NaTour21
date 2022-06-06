package com.example.natour21.entities;

import com.example.natour21.enums.DifficoltaItinerario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Itinerario {

    private long id;
    private String authorId;
    private String nome;
    private Double puntoInizialeLat;
    private Double puntoInizialeLong;
    private String nomePuntoIniziale;
    private int durata;
    private DifficoltaItinerario difficoltaItinerario;
    private String descrizione;
    @JsonProperty("tracciatoKey")
    private String tracciatoKey;
    @JsonProperty("isAccessibleMobilityImpairment")
    private Boolean isAccessibleMobilityImpairment;
    @JsonProperty("isAccessibleVisualImpairment")
    private Boolean isAccessibleVisualImpairment;

    public Itinerario(){}

    public Itinerario(long id, String authorId, String nome,
                      String nomePuntoIniziale, int durata, DifficoltaItinerario difficoltaItinerario) {
        this.id = id;
        this.authorId = authorId;
        this.nome = nome;
        this.nomePuntoIniziale = nomePuntoIniziale;
        this.durata = durata;
        this.difficoltaItinerario = difficoltaItinerario;
    }


    public Itinerario(long id, String authorId, String nome, Double puntoInizialeLat,
                      Double puntoInizialeLong, String nomePuntoIniziale, int durata,
                      DifficoltaItinerario difficoltaItinerario,
                      String descrizione, String tracciatoKey,
                      Boolean isAccessibleMobilityImpairment, Boolean isAccessibleVisualImpairment) {
        this.id = id;
        this.authorId = authorId;
        this.nome = nome;
        this.puntoInizialeLat = puntoInizialeLat;
        this.puntoInizialeLong = puntoInizialeLong;
        this.nomePuntoIniziale = nomePuntoIniziale;
        this.durata = durata;
        this.difficoltaItinerario = difficoltaItinerario;
        this.descrizione = descrizione;
        this.tracciatoKey = tracciatoKey;
        this.isAccessibleMobilityImpairment = isAccessibleMobilityImpairment;
        this.isAccessibleVisualImpairment = isAccessibleVisualImpairment;
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

    public void setDurata(int durata) {
        this.durata = durata;
    }

    public void setDifficoltaItinerario(DifficoltaItinerario difficoltaItinerario) {
        this.difficoltaItinerario = difficoltaItinerario;
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

    public String getDurataAsHourMinuteString() { return durata/60 + "h " + durata%60 + "m";}

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

    public Boolean getAccessibleMobilityImpairment() {
        return isAccessibleMobilityImpairment;
    }

    public void setAccessibleMobilityImpairment(Boolean accessibleMobilityImpairment) {
        isAccessibleMobilityImpairment = accessibleMobilityImpairment;
    }

    public Boolean getAccessibleVisualImpairment() {
        return isAccessibleVisualImpairment;
    }

    public void setAccessibleVisualImpairment(Boolean accessibleVisualImpairment) {
        isAccessibleVisualImpairment = accessibleVisualImpairment;
    }

    public void setTracciatoKey(String tracciatoKey) {
        this.tracciatoKey = tracciatoKey;
    }

    public void setNomePuntoIniziale(String nomePuntoIniziale) {
        this.nomePuntoIniziale = nomePuntoIniziale;
    }
}