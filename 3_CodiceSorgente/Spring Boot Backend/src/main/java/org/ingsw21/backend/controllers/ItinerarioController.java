package org.ingsw21.backend.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.ingsw21.backend.exceptions.*;
import org.ingsw21.backend.DAOFactories.DAOFactory;
import org.ingsw21.backend.DAOs.DAOItinerario;
import org.ingsw21.backend.entities.Itinerario;
import org.ingsw21.backend.entities.Utente;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/itinerario")
public class ItinerarioController {

	@Autowired
	private DAOFactory DAOFactory;
	
	private DAOItinerario DAOItinerario;
	
	private enum ItinerarioGetQuery{
		ID,
		GET_N_ITINERARIO,
		GET_N_ITINERARIO_STARTING_FROM,
		GET_N_ITINERARIO_NEWER_THAN,
		GET_ITINERARIO_BY_UTENTE
	}
	
	@GetMapping 
	public List<Itinerario> getItinerario
	(
			@RequestParam(required = false) Long id,
			@RequestParam(required = false) Integer numberToGet,
			@RequestParam(required = false) Long idToStartFrom,
			@RequestParam(required = false) Long newestId,
			@RequestParam(required = false) String utenteId
	) throws Exception
	{
		ItinerarioGetQuery itinerarioGetQueryType;
		itinerarioGetQueryType = decideGetQueryType(id, numberToGet, idToStartFrom, newestId, utenteId);
		if (itinerarioGetQueryType == null) throw new BadRequestWebException();
		try {
			DAOItinerario = DAOFactory.getDAOItinerario();
			List<Itinerario> result = new LinkedList<Itinerario>();
			if (itinerarioGetQueryType.equals(ItinerarioGetQuery.ID)){
				result.add(DAOItinerario.getItinerarioById(id));
				
			}
			else if (itinerarioGetQueryType.equals(ItinerarioGetQuery.GET_N_ITINERARIO)) {
				result.addAll(DAOItinerario.getLastNItinerario(numberToGet));
			}
			else if (itinerarioGetQueryType.equals(ItinerarioGetQuery.GET_N_ITINERARIO_NEWER_THAN)) {
				result.addAll(DAOItinerario.getLastNItinerarioNewerThan(newestId, numberToGet));
			}
			else if (itinerarioGetQueryType.equals(ItinerarioGetQuery.GET_N_ITINERARIO_STARTING_FROM)) {
				result.addAll(DAOItinerario.getLastNItinerarioStartingFrom(idToStartFrom, numberToGet));
			}
			else if (itinerarioGetQueryType.equals(ItinerarioGetQuery.GET_ITINERARIO_BY_UTENTE)) {
				result.addAll(DAOItinerario.getItinerarioByUtenteId(utenteId));
			}
			return result;
		}
		catch (WrappedCRUDException wcrude) {
			throw (wcrude.getWrappedException());
		}
			
	
	}
	
	@PostMapping
	public boolean insertItinerario
	(
			@RequestBody Itinerario itinerario
	) throws Exception
	{
		try {
			DAOItinerario = DAOFactory.getDAOItinerario();
			DAOItinerario.insertItinerario(itinerario);
			return true;
		}
		catch (WrappedCRUDException wcrude) {
			throw (wcrude.getWrappedException());
		}
	}
	
	public ItinerarioGetQuery decideGetQueryType(Long id, Integer numberToGet, Long idToStartFrom, Long newestId, 
			String utenteId) {
		if (id != null && id > 0) return ItinerarioGetQuery.ID;
		else if (utenteId != null) return ItinerarioGetQuery.GET_ITINERARIO_BY_UTENTE;
		else if ( (numberToGet == null || numberToGet < 1) ) return null;
		else if (idToStartFrom != null && idToStartFrom > -1) return ItinerarioGetQuery.GET_N_ITINERARIO_STARTING_FROM;
		else if (newestId != null && newestId > 0) return ItinerarioGetQuery.GET_N_ITINERARIO_NEWER_THAN;
		else return ItinerarioGetQuery.GET_N_ITINERARIO;
	}
}
