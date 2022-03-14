package org.ingsw21.backend.DAOs;

import java.util.List;

import org.ingsw21.backend.entities.Itinerario;
import org.ingsw21.backend.exceptions.WrappedCRUDException;
import org.springframework.data.geo.Point;

public interface DAOItinerario {

	public void insertItinerario(Itinerario itinerario) throws WrappedCRUDException;
	public void deleteItinerario(Itinerario itinerario) throws WrappedCRUDException;
	public void updateItinerario(Itinerario itinerario) throws WrappedCRUDException;
	public Itinerario getItinerarioById(long idItinerario) throws WrappedCRUDException;
	public List<Itinerario> getItinerarioByNome(String nomeItinerario) throws WrappedCRUDException;
	public List<Itinerario> getItinerarioByPuntoIniziale(Point puntoIniziale) throws WrappedCRUDException;
}
