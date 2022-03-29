package com.example.natour21.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;



public class Messaggio {

    private long id;
    private String testo;
    private OffsetDateTime timestamp;
    @JsonProperty("isUtenteOneSender")
    private boolean isUtenteOneSender;

    public Messaggio(){}

    public Messaggio(long id, String testo, OffsetDateTime timestamp, boolean isUtenteOneSender) {
        this.id = id;
        this.testo = testo;
        this.timestamp = timestamp;
        this.isUtenteOneSender = isUtenteOneSender;
    }

    public long getId() {
        return id;
    }

    public String getTesto() {
        return testo;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;  /// .toString()
    }

    public boolean isUtenteOneSender() {
        return isUtenteOneSender;
    }
}
