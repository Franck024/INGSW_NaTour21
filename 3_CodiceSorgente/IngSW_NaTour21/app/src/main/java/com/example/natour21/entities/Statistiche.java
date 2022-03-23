package com.example.natour21.entities;

public class Statistiche {

    private long utenteCount;
    private long utenteAccessCount;
    private long itinerarioCount;
    private long chatCount;
    private long messaggioCount;

    public Statistiche(){}
    public Statistiche(long utenteCount, long utenteAccessCount, long itinerarioCount, long chatCount, long messaggioCount) {
        this.utenteCount = utenteCount;
        this.utenteAccessCount = utenteAccessCount;
        this.itinerarioCount = itinerarioCount;
        this.chatCount = chatCount;
        this.messaggioCount = messaggioCount;
    }

    public long getUtenteCount() {
        return utenteCount;
    }

    public long getUtenteAccessCount() {
        return utenteAccessCount;
    }

    public long getItinerarioCount() {
        return itinerarioCount;
    }

    public long getChatCount() {
        return chatCount;
    }

    public long getMessaggioCount() {
        return messaggioCount;
    }
}
