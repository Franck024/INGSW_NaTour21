package org.ingsw21.backend.DAOSQL;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.ingsw21.backend.DAOs.DAOItinerario;
import org.ingsw21.backend.configurations.JDBCDataSourceConfig;
import org.ingsw21.backend.entities.Itinerario;
import org.ingsw21.backend.entities.Utente;
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
	getItinerarioByIdStatement,
	getLastNItinerarioStatement, 
	getLastNItinerarioStartingFromStatement,
	getLastNItinerarioNewerThanStatement,
	getItinerarioByUtenteStatement,
	getUniqueTracciatoKeyStatement;
	
	private String attributes = "id, authorId, nome, durata, "
			+ "ST_X(puntoIniziale::geometry) AS puntoInizialeLong, ST_Y(puntoIniziale::geometry) AS puntoInizialeLat,"
			+ "nomePuntoIniziale, difficolta, descrizione, tracciatoKey, isAccessibleMobilityImpairment,"
			+ "isAccessibleVisualImpairment";
	
	public DAOSQLItinerario() {
		insertItinerarioStatement = "INSERT INTO Itinerario VALUES"
				+ "(DEFAULT, ?, ?, ?, ?::geography, ?, ?::difficoltaItinerario, ?, ?, ?, ?)";
		getItinerarioByIdStatement = "SELECT " + attributes + " FROM Itinerario AS I WHERE I.id = ?";
		getLastNItinerarioStatement = "SELECT " + attributes + " FROM get_last_n_itinerario(?)";
		getLastNItinerarioStartingFromStatement = "SELECT " + attributes + 
				" FROM get_last_n_itinerario_starting_from(?, ?)";
		getLastNItinerarioNewerThanStatement = "SELECT " + attributes + " FROM get_last_n_itinerario_newer_than(?, ?)";
		getItinerarioByUtenteStatement = "SELECT " + attributes + " FROM Itinerario AS I WHERE I.authorId = ?";
		getUniqueTracciatoKeyStatement = "SELECT ('tracciato/' || gen_random_uuid::text) AS tracciatoKey FROM gen_random_uuid()";
	
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
			itinerario.setPuntoInizialeLat(rs.getDouble("puntoInizialeLat"));
			itinerario.setPuntoInizialeLong(rs.getDouble("puntoInizialeLong"));
			itinerario.setNomePuntoIniziale(rs.getString("nomePuntoIniziale"));
			itinerario.setDurata(rs.getInt("durata"));
			itinerario.setDifficoltaItinerario(DifficoltaItinerario.valueOf(rs.getString("difficolta")));
			itinerario.setDescrizione(rs.getString("descrizione"));
			itinerario.setTracciatoKey(rs.getString("tracciatoKey"));
			itinerario.setIsAccessibleMobilityImpairment(rs.getBoolean("isAccessibleMobilityImpairment"));
			itinerario.setIsAccessibleVisualImpairment(rs.getBoolean("isAccessibleVisualImpairment"));
			return itinerario;
		}
	}

	@Override
	public void insertItinerario(Itinerario itinerario) throws WrappedCRUDException {
	
		String pointString;
		Double pointLong = itinerario.getPuntoInizialeLong();
		Double pointLat = itinerario.getPuntoInizialeLat();
		pointString = (pointLong != null && pointLat != null) ? "POINT(" + itinerario.getPuntoInizialeLong() 
		+ " " + itinerario.getPuntoInizialeLat() + ")" : null;
		try {
			jdbcTemplate.update(insertItinerarioStatement,
					itinerario.getAuthorId(),
					itinerario.getNome(),
					itinerario.getDurata(),
					pointString,
					itinerario.getNomePuntoIniziale(),
					itinerario.getDifficoltaItinerario().toString(),
					itinerario.getDescrizione(),
					itinerario.getTracciatoKey(),
					itinerario.getIsAccessibleMobilityImpairment(),
					itinerario.getIsAccessibleVisualImpairment());
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
	public List<Itinerario> getLastNItinerario(int n) throws WrappedCRUDException {
		try {
			return jdbcTemplate.query(getLastNItinerarioStatement, new ItinerarioMapper(), n);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}

	@Override
	public List<Itinerario> getLastNItinerarioStartingFrom(long startingFrom, int n) throws WrappedCRUDException {
		try {
			return jdbcTemplate.query(getLastNItinerarioStartingFromStatement, new ItinerarioMapper(),
					startingFrom,
					n);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}
	
	@Override
	public List<Itinerario> getItinerarioByUtenteId(String utenteId) throws WrappedCRUDException {
		try {
			return jdbcTemplate.query(getItinerarioByUtenteStatement, new ItinerarioMapper(),
					utenteId);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}
	
	@Override
	public List<Itinerario> getLastNItinerarioNewerThan(long newestId, int n) throws WrappedCRUDException {
		try {
			return jdbcTemplate.query(getLastNItinerarioNewerThanStatement, new ItinerarioMapper(),
					newestId,
					n);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}
	
	@Override
	public List<Itinerario> getItinerarioByProperties(Double pointLat, Double pointLong, Double distanceWithin,
			Boolean[] difficoltaArray, Integer durationToBeCompared, Boolean shouldBeLessThanGivenDuration,
			Boolean isAccessibleMobilityImpairment, Boolean isAccessibleVisualImpairment) throws WrappedCRUDException {
		String finalQuery = "SELECT " + attributes + " FROM Itinerario";
		String additionalParams = "";
		boolean isFirstParamAdded = true;
		if (pointLat != null && pointLong != null && distanceWithin != null){
			additionalParams += " ST_DWithin('POINT("
			+ pointLong + " " + pointLat + ")'::geography, puntoIniziale," + distanceWithin + ")";
			isFirstParamAdded = false;
		}
		if (difficoltaArray != null) {
			DifficoltaItinerario[] difficoltaItinerarioValuesArray = DifficoltaItinerario.values();
			int difficoltaParamCount = 0;
			for (int i = 0; i < difficoltaItinerarioValuesArray.length; i++){
				if (difficoltaArray[i] != null && difficoltaArray[i] == true){
					if (difficoltaParamCount == 0){
						if (!isFirstParamAdded) {
							additionalParams += " AND";
						} else isFirstParamAdded = false;
						additionalParams += " (";
					}
					else additionalParams += " OR";
					difficoltaParamCount++;
					additionalParams += " difficolta::text LIKE '" + 
					difficoltaItinerarioValuesArray[i].toString().toLowerCase() + "'";
				}
				
			}
			if (difficoltaParamCount > 0) additionalParams += ")";
		}
		if (durationToBeCompared != null && shouldBeLessThanGivenDuration != null){
			if (!isFirstParamAdded) additionalParams += " AND";
			else isFirstParamAdded = false;
			additionalParams += " durata " + (shouldBeLessThanGivenDuration ? "< " : "> ") + durationToBeCompared;
		}
		
		if (isAccessibleMobilityImpairment != null){
			if (!isFirstParamAdded) additionalParams += " AND";
			else isFirstParamAdded = false;
			additionalParams += " isAccessibleMobilityImpairment = " + isAccessibleMobilityImpairment;
		}
		
		if (isAccessibleVisualImpairment != null){
			if (!isFirstParamAdded) additionalParams += " AND";
			else isFirstParamAdded = false;
			additionalParams += " isAccessibleVisualImpairment = " + isAccessibleVisualImpairment;
		}
		
		if (!isFirstParamAdded) finalQuery += " WHERE" + additionalParams;
		System.out.println(finalQuery);
		try {
			return jdbcTemplate.query(finalQuery, new ItinerarioMapper());
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}
	
	@Override
	public String getUniqueTracciatoKey() throws WrappedCRUDException{
		try {
			List<String> result = jdbcTemplate.query(getUniqueTracciatoKeyStatement, new RowMapper<String>() {

				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("tracciatoKey");
				}
				
			});
			if (result == null || result.isEmpty()) return null;
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
