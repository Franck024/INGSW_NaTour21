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
		ID
	}
	
	@GetMapping 
	public List<Itinerario> getItinerario
	(
			@RequestParam long id
	) throws Exception
	{
		if (id > 0) {
			DAOItinerario = DAOFactory.getDAOItinerario();
			try {
				List<Itinerario> result = new LinkedList<Itinerario>();
				result.add(DAOItinerario.getItinerarioById(id));
				return result;
			}
			catch (WrappedCRUDException wcrude) {
				throw (wcrude.getWrappedException());
			}
		} else throw new BadRequestWebException("Invalid id :" + id);
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
}
