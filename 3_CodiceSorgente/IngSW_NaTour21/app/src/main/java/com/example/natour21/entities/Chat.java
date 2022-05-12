package com.example.natour21.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Chat {

    private String utenteOneId;
    private String utenteTwoId;
    private ArrayList<Messaggio> messaggi = new ArrayList<Messaggio>();

    public Chat(){}

    public Chat(String utenteOneId, String utenteTwoId) {
        this.utenteOneId = utenteOneId;
        this.utenteTwoId = utenteTwoId;
    }

    public Chat(String utenteOneId, String utenteTwoId, ArrayList<Messaggio> messaggi) {
        this.utenteOneId = utenteOneId;
        this.utenteTwoId = utenteTwoId;
        this.messaggi = messaggi;
    }

    public int getNumberOfMessaggio() {
        return (messaggi == null) ? 0 : messaggi.size();
    }

    //both input values are inclusive
    public LinkedList<Messaggio> getMessaggioInRange(int startRange, int endRange) throws IndexOutOfBoundsException{
        if (messaggi == null) return null;
        if (startRange >= endRange || startRange < 0 || endRange >= messaggi.size()) return null;
        LinkedList<Messaggio> result = new LinkedList<Messaggio>();
        for (int i = startRange; i <= endRange; i++) {
            result.add(messaggi.get(i));
        }
        return result;
    }

    public void addMessaggio(List<Messaggio> messaggio) {
        if (messaggio == null) messaggi = new ArrayList<Messaggio>();
        messaggi.addAll(messaggio);
    }

    public String getUtenteOneId() {
        return utenteOneId;
    }

    public String getUtenteTwoId() {
        return utenteTwoId;
    }

    public ArrayList<Messaggio> getMessaggi() {
        return messaggi;
    }

    public void setMessaggi(ArrayList<Messaggio> messaggi) {
        this.messaggi = messaggi;
    }
}
