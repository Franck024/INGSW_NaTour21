package org.ingsw21.backend.DAOs;
import java.util.List;
import org.ingsw21.backend.entities.Utente;
import org.ingsw21.backend.exceptions.WrappedCRUDException;

public interface DAOUtente{
	
	public void insertUtente(Utente utente) throws WrappedCRUDException;
	public void deleteUtente(Utente utente) throws WrappedCRUDException;
	public void updateUtente(Utente utente) throws WrappedCRUDException;
	public Utente getUtenteByEmail(String email) throws WrappedCRUDException;
	public List<Utente> getUtenteByCitta(String citta) throws WrappedCRUDException;
	
}
