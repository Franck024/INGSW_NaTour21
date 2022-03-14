package org.ingsw21.backend.DAOSQL;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.ingsw21.backend.DAOs.DAOItinerario;
import org.ingsw21.backend.configurations.JDBCDataSourceConfig;
import org.ingsw21.backend.entities.Itinerario;
import org.ingsw21.backend.enums.DifficoltaItinerario;
import org.ingsw21.backend.exceptions.WrappedCRUDException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Point;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
@Repository
public class DAOSQLItinerario implements DAOItinerario {

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	private String insertItinerarioStatement,
	getItinerarioByIdStatement;
	
	public DAOSQLItinerario() {
		insertItinerarioStatement = "INSERT INTO Itinerario VALUES(DEFAULT, ?, ?, ?, ?, ?::difficoltaItinerario, ?, ?)";
		getItinerarioByIdStatement = "SELECT * FROM Itinerario AS I WHERE I.id = ?";
	}
	
	@PostConstruct
	private void initJdbcTemplate() {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private static final class ItinerarioMapper implements RowMapper<Itinerario>{
		
		public Itinerario mapRow(ResultSet rs, int rowNum) throws SQLException{
			Itinerario itinerario = new Itinerario();
			itinerario.setId(rs.getLong("id"));
			itinerario.setAuthorId(rs.getString("authorId"));
			itinerario.setNome(rs.getString("nome"));
			itinerario.setNomePuntoIniziale(rs.getString("nomePuntoIniziale"));
			itinerario.setDurata(rs.getInt("durata"));
			itinerario.setDifficoltaItinerario(DifficoltaItinerario.valueOf(rs.getString("difficolta")));
			itinerario.setDescrizione(rs.getString("descrizione"));
			itinerario.setTracciatoKey(rs.getString("tracciatoKey"));
			return itinerario;
		}
	}

	@Override
	public void insertItinerario(Itinerario itinerario) throws WrappedCRUDException {
		try {
			jdbcTemplate.update(insertItinerarioStatement,
					itinerario.getAuthorId(),
					itinerario.getNome(),
					itinerario.getDurata(),
					itinerario.getNomePuntoIniziale(),
					itinerario.getDifficoltaItinerario().toString(),
					itinerario.getDescrizione(),
					itinerario.getTracciatoKey());
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}
	
	@Override
	public Itinerario getItinerarioById(long idItinerario) throws WrappedCRUDException {
		try {
			List<Itinerario> result = jdbcTemplate.query
					(getItinerarioByIdStatement, new ItinerarioMapper(), idItinerario);
			if (result.isEmpty()) return null;
			else return result.get(0);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
		
	}

	@Override
	public void deleteItinerario(Itinerario itinerario) throws WrappedCRUDException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateItinerario(Itinerario itinerario) throws WrappedCRUDException {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public List<Itinerario> getItinerarioByNome(String nomeItinerario) throws WrappedCRUDException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Itinerario> getItinerarioByPuntoIniziale(Point puntoIniziale) throws WrappedCRUDException {
		// TODO Auto-generated method stub
		return null;
	}
}
