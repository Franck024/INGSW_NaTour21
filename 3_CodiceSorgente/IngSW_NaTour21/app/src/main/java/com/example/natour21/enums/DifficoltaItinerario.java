package com.example.natour21.enums;

public enum DifficoltaItinerario {
    facile,
    media,
    difficile;

    @Override
    public String toString(){
        if (this.equals(facile)) return "Facile";
        if (this.equals(media)) return "Media";
        if (this.equals(difficile)) return "Difficile";
        return "INVALID";
    }
}