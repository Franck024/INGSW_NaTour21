package com.example.natour21.DTOs;

import com.example.natour21.entities.Messaggio;

public class DTOMessaggio {

    private String sender;
    private Messaggio messaggio;

    public DTOMessaggio(){}

    public DTOMessaggio(String sender, Messaggio messaggio) {
        this.sender = sender;
        this.messaggio = messaggio;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Messaggio getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(Messaggio messaggio) {
        this.messaggio = messaggio;
    }
}

