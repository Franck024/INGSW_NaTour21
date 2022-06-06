package org.ingsw21.backend.DAOs;

import java.util.List;

import org.ingsw21.backend.entities.Itinerario;
import org.ingsw21.backend.entities.Utente;
import org.ingsw21.backend.exceptions.WrappedCRUDException;
import org.springframework.data.geo.Point;

public interface DAOItinerario {

	public void insertItinerario(Itinerario itinerario) throws WrappedCRUDException;
	public void deleteItinerario(Itinerario itinerario) throws WrappedCRUDException;
	public void updateItinerario(Itinerario itinerario) throws WrappedCRUDException;
	public Itinerario getItinerarioById(long idItinerario) throws WrappedCRUDException;
	public List<Itinerario> getItinerarioByNome(String nomeItinerario) throws WrappedCRUDException;
	public List<Itinerario> getItinerarioByPuntoIniziale(Point puntoIniziale) throws WrappedCRUDException;
	public List<Itinerario> getLastNItinerario(int n) throws WrappedCRUDException;
	public List<Itinerario> getLastNItinerarioStartingFrom(long startingFrom, int n) throws WrappedCRUDException;
	public List<Itinerario> getLastNItinerarioNewerThan(long newestId, int n) throws WrappedCRUDException;
	public List<Itinerario> getItinerarioByUtenteId(String utenteId) throws WrappedCRUDException;
	public String getUniqueTracciatoKey() throws WrappedCRUDException;
	//Ottiene un itinerario che abbia certe proprietà. Ogni proprietà è omittibile.
	//1. Distanza da un punto.
	//2. Livello di difficoltà (più di uno specificabile). Se si vuole che vengano cercati itinerari con certi livelli
	//di difficoltà, bisogna settare a true la flag nella posizione dell'array 
	//dell'ordinale dell'enum difficoltà desiderato.
	//3. durata, che può essere minore o maggiore di quella specificata.
	//4. accessibile a chi è impedito motoriamente
	//5. accessibile a chi è impedito visivamente
	public List<Itinerario> getItinerarioByProperties
		(
				Double pointLat, Double pointLong, Double distanceWithin, 
				Boolean[] difficoltaArray,
				Integer durationToBeCompared, Boolean shouldBeLessThanGivenDuration,
				Boolean isAccessibleMobilityImpairment,
				Boolean isAccessibleVisualImpairment
		) throws WrappedCRUDException;
}
