package org.ingsw21.backend.BackEnd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.CoreMatchers.is;

import org.ingsw21.backend.DAOSQL.DAOSQLUtente;
import org.ingsw21.backend.entities.Utente;
import org.ingsw21.backend.exceptions.WrappedCRUDException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
@SpringBootTest
public class DAOSQLUtenteTest {

	@Autowired
	DAOSQLUtente DAOSQLUtente;
	
	boolean insertTestSuccessfulFlag = false;
	Utente testInsertUtente;
	
	@BeforeEach
	private void resetInsertTestSuccessfulFlag(){
		insertTestSuccessfulFlag = false;
	}
	
	@AfterEach
	private void cleanUpInsertedUtente() {
		if (insertTestSuccessfulFlag && testInsertUtente != null) {
			try {
				DAOSQLUtente.deleteUtente(testInsertUtente);
			}
			catch (WrappedCRUDException wcrude) {
				System.out.println(wcrude.getWrappedException().getMessage());
			}
		}
	}
	
	@Test
	public void testInserzioneUtenteValido() {
		testInsertUtente = new Utente("testmail@springtest.com", "Spring" , "Man", false);
		try {
			DAOSQLUtente.insertUtente(testInsertUtente);
			String email = testInsertUtente.getEmail();
			String resultEmail = DAOSQLUtente.getUtenteByEmail(email).getEmail();
			if (email.equals(resultEmail)) {
				insertTestSuccessfulFlag = true;
				return;
			}
			else fail("Utente inserito: "+ email + "\nUtente risultante: " + resultEmail);
		}
		catch (WrappedCRUDException wcrude) {
			fail(wcrude.getWrappedException().getMessage());
		}
		
	}
	
	@Test
	public void testInserzioneUtenteNull() {
		Exception exception = Assertions.assertThrows(NullPointerException.class, () ->  DAOSQLUtente.insertUtente(null));
		if (exception instanceof NullPointerException) return;
		else fail( (exception == null) ? "Nessuna eccezione tirata." : exception.getMessage());
	}
	
	@Test
	public void testInserzioneUtenteConEmailNonFormattata() {
		testInsertUtente = new Utente("emailSbagliata.com", "Hackerino", "Sospettoso", false);
		Exception exception = Assertions.assertThrows(NullPointerException.class, 
				() ->  DAOSQLUtente.insertUtente(testInsertUtente));
		if (exception instanceof WrappedCRUDException) return;
		insertTestSuccessfulFlag = true;
		fail( (exception == null) ? "Nessuna eccezione tirata." : exception.getMessage());
	}
}
