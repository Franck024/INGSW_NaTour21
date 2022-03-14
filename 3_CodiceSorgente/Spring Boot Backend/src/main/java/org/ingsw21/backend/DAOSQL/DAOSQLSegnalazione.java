package org.ingsw21.backend.DAOSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.ingsw21.backend.DAOs.DAOSegnalazione;
import org.ingsw21.backend.DAOs.DAOUtente;
import org.ingsw21.backend.configurations.JDBCDataSourceConfig;
import org.ingsw21.backend.entities.Itinerario;
import org.ingsw21.backend.entities.Segnalazione;
import org.ingsw21.backend.exceptions.WrappedCRUDException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DAOSQLSegnalazione implements DAOSegnalazione {

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	private String insertSegnalazioneStatement,
	deleteSegnalazioneStatement,
	getSegnalazioneByItinerarioStatement,
	updateSegnalazioneStatement;
	
	public DAOSQLSegnalazione() {
		insertSegnalazioneStatement = "INSERT INTO Segnalazione VALUES(?, ?, ?, ?)";
		deleteSegnalazioneStatement = "DELETE FROM Segnalazione WHERE authorId = ? AND itinerarioId = ?";
		getSegnalazioneByItinerarioStatement = "SELECT * FROM Segnalazione WHERE itinerarioId = ?";
		updateSegnalazioneStatement = "UPDATE Segnalazione SET titolo = ?, descrizione = ? "
				+ "WHERE authorId = ? AND itinerarioId = ?";
	}
	
	@PostConstruct
	private void initJdbcTemplate() {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private static final class SegnalazioneMapper implements RowMapper<Segnalazione>{
		
		public Segnalazione mapRow(ResultSet rs, int rowNum) throws SQLException{
			Segnalazione segnalazione = new Segnalazione();
			segnalazione.setAuthorId(rs.getString("authorId"));
			segnalazione.setItinerarioId(rs.getLong("itinerarioId"));
			segnalazione.setTitolo(rs.getString("titolo"));
			segnalazione.setDescrizione(rs.getString("descrizione"));
			return segnalazione;
		}
	}

	@Override
	public void insertSegnalazione(Segnalazione segnalazione) throws WrappedCRUDException {
		try {
			jdbcTemplate.update(insertSegnalazioneStatement,
					segnalazione.getAuthorId(),
					segnalazione.getItinerarioId(),
					segnalazione.getTitolo(),
					segnalazione.getDescrizione());
		}
		catch(DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
		
	}
	
	@Override
	public List<Segnalazione> getSegnalazioneByItinerario(Itinerario itinerario) throws WrappedCRUDException {
		try {
			return jdbcTemplate.query(getSegnalazioneByItinerarioStatement, new SegnalazioneMapper(), itinerario.getId());
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}

	@Override
	public void deleteSegnalazione(Segnalazione segnalazione) throws WrappedCRUDException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateSegnalazione(Segnalazione segnalazione) throws WrappedCRUDException {
		// TODO Auto-generated method stub
		
	}

	
	
}
