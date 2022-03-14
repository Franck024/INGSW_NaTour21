package org.ingsw21.backend.DAOSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.ingsw21.backend.DAOs.DAOUtente;
import org.ingsw21.backend.configurations.JDBCDataSourceConfig;
import org.ingsw21.backend.entities.Utente;
import org.ingsw21.backend.exceptions.WrappedCRUDException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DAOSQLUtente implements DAOUtente {

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	private String insertUtenteStatement,
	deleteUtenteStatement,
	updateUtenteStatement,
	getUtenteByEmailStatement,
	getUtenteByCittaStatement;
	
	public DAOSQLUtente() {
		insertUtenteStatement = "INSERT INTO Utente VALUES(?, ?, ?, ?, ?, ?)";
		deleteUtenteStatement = "DELETE FROM Utente WHERE email = ?";
		updateUtenteStatement = "UPDATE Utente SET nome = ?, cognome = ?, cellulare = ?, citta = ?, isAdmin = ?"
				+ "WHERE email = ?";
		getUtenteByEmailStatement = "SELECT * FROM Utente WHERE email = ?";
		getUtenteByCittaStatement = "SELECT * FROM Utente WHERE citta = ?";
	}
	
	@PostConstruct
	private void initJdbcTemplate() {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private static final class UtenteMapper implements RowMapper<Utente>{
		
		public Utente mapRow(ResultSet rs, int rowNum) throws SQLException{
			Utente utente = new Utente();
			utente.setEmail(rs.getString("email"));
			utente.setNome(rs.getString("nome"));
			utente.setCognome(rs.getString("cognome"));
			utente.setCellulare(rs.getString("cellulare"));
			utente.setCitta(rs.getString("citta"));
			utente.setAdmin(rs.getBoolean("isAdmin"));
			return utente;
		}
	}
	
	@Override
	public void insertUtente(Utente utente) throws WrappedCRUDException {
		try {
			jdbcTemplate.update(insertUtenteStatement,
					utente.getEmail(), 
					utente.getNome(), 
					utente.getCognome(), 
					utente.getCellulare(), 
					utente.getCitta(),
					utente.isAdmin());
		}
		catch(DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
		
	}

	@Override
	public void deleteUtente(Utente utente) throws WrappedCRUDException {
		try {
			jdbcTemplate.update(deleteUtenteStatement,
					utente.getEmail());
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
		
	}

	@Override
	public void updateUtente(Utente utente) throws WrappedCRUDException {
		try {
			jdbcTemplate.update(updateUtenteStatement,
					utente.getNome(),
					utente.getCognome(),
					utente.getCellulare(),
					utente.getCitta(),
					utente.isAdmin(),
					utente.getEmail());
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
		
	}

	@Override
	public Utente getUtenteByEmail(String email) throws WrappedCRUDException {
		try {
			List<Utente> result = jdbcTemplate.query(getUtenteByEmailStatement, new UtenteMapper(), email);
			if (result.isEmpty()) return null;
			else return result.get(0);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}

	@Override
	public List<Utente> getUtenteByCitta(String citta) throws WrappedCRUDException {
		try {
			return jdbcTemplate.query(getUtenteByCittaStatement, new UtenteMapper(), citta);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}
	
	

}
