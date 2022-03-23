package org.ingsw21.backend.DAOs;

import org.ingsw21.backend.entities.Statistiche;
import org.ingsw21.backend.exceptions.WrappedCRUDException;

public interface DAOStatistiche {
	
	public void incrementUtenteAccess() throws WrappedCRUDException;
	public Statistiche getStatistiche() throws WrappedCRUDException;
}
