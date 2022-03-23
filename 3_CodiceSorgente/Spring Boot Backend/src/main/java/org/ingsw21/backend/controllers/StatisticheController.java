package org.ingsw21.backend.controllers;

import org.ingsw21.backend.DAOs.DAOStatistiche;
import org.ingsw21.backend.entities.Statistiche;
import org.ingsw21.backend.exceptions.WrappedCRUDException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ingsw21.backend.DAOFactories.DAOFactory;

@RestController
@RequestMapping("/statistiche")
public class StatisticheController {
	
	@Autowired
	DAOFactory DAOFactory;
	
	private DAOStatistiche DAOStatistiche;
	
	@GetMapping
	public Statistiche getStatistiche() throws Exception {
		try {
			DAOStatistiche = DAOFactory.getDAOStatistiche();
			return DAOStatistiche.getStatistiche();
		}
		catch (WrappedCRUDException wcrude) {
			throw (wcrude.getWrappedException());
		}
	}
	
	@PostMapping
	public boolean incrementAccessCount() throws Exception{
		try {
			DAOStatistiche = DAOFactory.getDAOStatistiche();
			DAOStatistiche.incrementUtenteAccess();
			return true;
		}
		catch (WrappedCRUDException wcrude) {
			throw (wcrude.getWrappedException());
		}
	}

}
