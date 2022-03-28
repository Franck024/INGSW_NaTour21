package org.ingsw21.backend.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.ingsw21.backend.exceptions.*;
import org.ingsw21.backend.DAOFactories.DAOFactory;
import org.ingsw21.backend.DAOs.DAOItinerario;
import org.ingsw21.backend.entities.Itinerario;

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
		GET_N_ITINERARIO_STARTING_FROM
	}
	
	@GetMapping 
	public List<Itinerario> getItinerario
	(
			@RequestParam(required = false) Long id,
			@RequestParam(required = false) Integer numberToGet,
			@RequestParam(required = false) Long idToStartFrom
	) throws Exception
	{
		ItinerarioGetQuery itinerarioGetQueryType;
		itinerarioGetQueryType = decideGetQueryType(id, numberToGet, idToStartFrom);
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
			else if (itinerarioGetQueryType.equals(ItinerarioGetQuery.GET_N_ITINERARIO_STARTING_FROM)) {
				result.addAll(DAOItinerario.getLastNItinerarioStartingFrom(idToStartFrom, numberToGet));
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
	
	public ItinerarioGetQuery decideGetQueryType(Long id, Integer numberToGet, Long idToStartFrom) {
		if (id != null && id > 0) return ItinerarioGetQuery.ID;
		else if ( (numberToGet == null || numberToGet < 1) ) return null;
		else if (idToStartFrom == null) return ItinerarioGetQuery.GET_N_ITINERARIO;
		else return ItinerarioGetQuery.GET_N_ITINERARIO_STARTING_FROM;
	}
}
