package org.ingsw21.backend.DAOSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.ingsw21.backend.DAOs.DAOStatistiche;
import org.ingsw21.backend.entities.Statistiche;
import org.ingsw21.backend.exceptions.WrappedCRUDException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DAOSQLStatistiche implements DAOStatistiche{

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	private String increaseUtenteAccessCountStatement,
	getStatisticheStatement;
	
	public DAOSQLStatistiche() {
		increaseUtenteAccessCountStatement = "UPDATE Statistiche SET utenteAccessCount = utenteAccessCount + 1"
				+ "WHERE Statistiche.id = true";
		getStatisticheStatement = "SELECT * FROM Statistiche WHERE Statistiche.id = true";
	}
	
	@PostConstruct
	private void initJdbcTemplate() {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private final static class StatisticheMapper implements RowMapper<Statistiche>{
		public Statistiche mapRow(ResultSet rs, int rowNum) throws SQLException {
			Statistiche statistiche = new Statistiche();
			statistiche.setChatCount(rs.getLong("chatCount"));
			statistiche.setItinerarioCount(rs.getLong("itinerarioCount"));
			statistiche.setMessaggioCount(rs.getLong("messaggioCount"));
			statistiche.setUtenteAccessCount(rs.getLong("utenteAccessCount"));
			statistiche.setUtenteCount(rs.getLong("utenteCount"));
			return statistiche;
		}
	}

	@Override
	public void incrementUtenteAccess() throws WrappedCRUDException {
		try {
			jdbcTemplate.update(increaseUtenteAccessCountStatement);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
		
	}

	@Override
	public Statistiche getStatistiche() throws WrappedCRUDException {
		try {
			List<Statistiche> result = jdbcTemplate.query(getStatisticheStatement, new StatisticheMapper());
			if (result == null) return null;
			else if (result.size() == 0) return null;
			else return result.get(0);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}
	
}
