package org.ingsw21.backend.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.ingsw21.backend.exceptions.*;
import org.ingsw21.backend.DAOFactories.DAOFactory;
import org.ingsw21.backend.DAOs.DAOSegnalazione;
import org.ingsw21.backend.entities.Itinerario;
import org.ingsw21.backend.entities.Segnalazione;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/segnalazione")
public class SegnalazioneController {
	
	@Autowired
	private DAOFactory DAOFactory;
	
	private DAOSegnalazione DAOSegnalazione;
	private enum SegnalazioneGetQuery{
		ITINERARIO_ID
	}
	
	@GetMapping
	public List<Segnalazione> getSegnalazioni
	(
			@RequestParam long itinerarioId
	) throws Exception
	{
		SegnalazioneGetQuery segnalazioneGetQueryType;
		if (itinerarioId < 1) throw new BadRequestWebException("Invalid id: " + itinerarioId);
		else segnalazioneGetQueryType = SegnalazioneGetQuery.ITINERARIO_ID;
		try {
			
			//init
			DAOSegnalazione = DAOFactory.getDAOSegnalazione();
			LinkedList<Segnalazione> result = new LinkedList<Segnalazione>();
			
			//Query select logic
			if (segnalazioneGetQueryType == SegnalazioneGetQuery.ITINERARIO_ID) {
				Itinerario dummyItinerario = new Itinerario();
				dummyItinerario.setId(itinerarioId);
				result.addAll(DAOSegnalazione.getSegnalazioneByItinerario(dummyItinerario));
			}
			return result;
		}
		catch (WrappedCRUDException wcrude)
		{
			throw (wcrude.getWrappedException());
		}
	}
	
	@PostMapping
	public boolean insertSegnalazione(@RequestBody Segnalazione segnalazione) throws Exception {
		try {
			DAOSegnalazione = DAOFactory.getDAOSegnalazione();
			DAOSegnalazione.insertSegnalazione(segnalazione);
			return true;
		}
		catch (WrappedCRUDException wcrude) {
			throw (wcrude.getWrappedException());
		}
	}

}
