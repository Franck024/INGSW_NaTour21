package org.ingsw21.backend.DAOs;

import java.util.List;

import org.ingsw21.backend.entities.Itinerario;
import org.ingsw21.backend.entities.Segnalazione;
import org.ingsw21.backend.exceptions.WrappedCRUDException;

public interface DAOSegnalazione {

	public void insertSegnalazione(Segnalazione segnalazione) throws WrappedCRUDException;
	public void deleteSegnalazione(Segnalazione segnalazione) throws WrappedCRUDException;
	public void updateSegnalazione(Segnalazione segnalazione) throws WrappedCRUDException;
	public List<Segnalazione> getSegnalazioneByItinerario(Itinerario itinerario) throws WrappedCRUDException;
}
