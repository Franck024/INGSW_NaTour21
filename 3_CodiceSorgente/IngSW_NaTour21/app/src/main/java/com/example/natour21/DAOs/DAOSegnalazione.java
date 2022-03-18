package com.example.natour21.DAOs;

import com.example.natour21.entities.*;
import com.example.natour21.exceptions.WrappedCRUDException;
import java.util.List;

public interface DAOSegnalazione {

    public boolean insertSegnalazione(Segnalazione segnalazione) throws WrappedCRUDException;
    public void deleteSegnalazione(Segnalazione segnalazione) throws WrappedCRUDException;
    public void updateSegnalazione(Segnalazione segnalazione) throws WrappedCRUDException;
    public List<Segnalazione> getSegnalazioneByItinerario(Itinerario itinerario) throws WrappedCRUDException;
}
