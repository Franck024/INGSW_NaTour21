package org.ingsw21.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.ingsw21.backend.exceptions.*;
import org.ingsw21.backend.DAOFactories.DAOFactory;
import org.ingsw21.backend.DAOs.DAOUtente;
import org.ingsw21.backend.entities.Utente;

import java.util.LinkedList;
import java.util.List;



//@SpringBootApplication
@RestController
@RequestMapping("/utente")
public class UtenteController {
	
	@Autowired
	private DAOFactory DAOFactory;
	
	private DAOUtente DAOUtente;
	private enum UtenteGetQuery{
		EMAIL,
		CITTA
	}
	
	@GetMapping
	public List<Utente> getUtenti
	(
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String citta
	) throws Exception
	{
		UtenteGetQuery utenteGetQueryType;
		utenteGetQueryType = decideGetQueryType(email, citta);
		if (utenteGetQueryType == null) throw new BadRequestWebException();
		try {
				DAOUtente = DAOFactory.getDAOUtente();
				LinkedList<Utente> result = new LinkedList<Utente>();
				if (utenteGetQueryType == UtenteGetQuery.EMAIL) {
					result.add(DAOUtente.getUtenteByEmail(email));
				}
				else if (utenteGetQueryType == UtenteGetQuery.CITTA) {
					result.addAll(DAOUtente.getUtenteByCitta(citta));
				}
				return result;
		}
		catch (WrappedCRUDException wcrude)
		{
			throw (wcrude.getWrappedException());
		}
			
	}
	
	@PostMapping
	public boolean insertUtente(@RequestBody Utente utente) throws Exception {
		try {
			DAOUtente = DAOFactory.getDAOUtente();
			DAOUtente.insertUtente(utente);
		}
		catch (WrappedCRUDException wcrude) {
			throw (wcrude.getWrappedException());
		}
		return true;	
	}
	
	private UtenteGetQuery decideGetQueryType(String email, String citta) {
		if (email == null) {
			if (citta == null) return null;
			return UtenteGetQuery.CITTA;
		} else return UtenteGetQuery.EMAIL;
	}
	
}
