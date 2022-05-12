package com.example.natour21.DAOs;


import org.osmdroid.util.GeoPoint;
import com.example.natour21.entities.*;
import com.example.natour21.exceptions.WrappedCRUDException;

import java.util.List;
public interface DAOItinerario {

    public boolean insertItinerario(Itinerario itinerario) throws WrappedCRUDException;
    public void deleteItinerario(Itinerario itinerario) throws WrappedCRUDException;
    public void updateItinerario(Itinerario itinerario) throws WrappedCRUDException;
    public Itinerario getItinerarioById(long idItinerario) throws WrappedCRUDException;
    public List<Itinerario> getItinerarioByNome(String nomeItinerario) throws WrappedCRUDException;
    public List<Itinerario> getItinerarioByPuntoIniziale(GeoPoint puntoIniziale) throws WrappedCRUDException;
    public List<Itinerario> getLastNItinerario(int n) throws WrappedCRUDException;
    public List<Itinerario> getLastNItinerarioStartingFrom(long startingFrom, int n) throws WrappedCRUDException;
    public List<Itinerario> getLastNItinerarioNewerThan(long newestId, int n) throws WrappedCRUDException;
    public List<Itinerario> getItinerarioByUtenteId(String utenteId) throws WrappedCRUDException;
}
