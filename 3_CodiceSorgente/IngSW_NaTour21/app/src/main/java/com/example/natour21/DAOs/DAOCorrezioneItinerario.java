package com.example.natour21.DAOs;

import com.example.natour21.entities.CorrezioneItinerario;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.exceptions.WrappedCRUDException;

import java.util.List;

public interface DAOCorrezioneItinerario {

    public boolean insertCorrezioneItinerario(CorrezioneItinerario correzioneItinerario) throws WrappedCRUDException;
    public void deleteCorrezioneItinerario(CorrezioneItinerario correzioneItinerario) throws WrappedCRUDException;
    public void updateCorrezioneItinerario(CorrezioneItinerario correzioneItinerario) throws WrappedCRUDException;
    public List<CorrezioneItinerario> getCorrezioneItinerarioByItinerario(Itinerario itinerario) throws WrappedCRUDException;
}
