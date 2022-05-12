package org.ingsw21.backend.DAOSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.ingsw21.backend.DAOs.DAOChat;
import org.ingsw21.backend.configurations.JDBCDataSourceConfig;
import org.ingsw21.backend.entities.Messaggio;
import org.ingsw21.backend.entities.Utente;
import org.ingsw21.backend.entities.Chat;
import org.ingsw21.backend.exceptions.WrappedCRUDException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class DAOSQLChat implements DAOChat {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private DataSource dataSource;
	
	private String insertChatStatement,
	insertMessaggioStatement,
	getChatByUtenteStatement,
	getMessaggioByUtenteStatement,
	getLastMessaggioStatement,
	checkIfChatIsUpToDateStatement,
	getAllMessaggioStatement,
	getAllChatWithUtenteStatement;
	
	public DAOSQLChat() {
		insertChatStatement = "INSERT INTO Chat VALUES (?, ?, DEFAULT)";
		insertMessaggioStatement = "INSERT INTO Messaggio VALUES(DEFAULT, ?, ?, ?, ?, ?) RETURNING id";
		getChatByUtenteStatement = "SELECT * FROM Chat WHERE utenteOne = ? AND utenteTwo = ?";
		getMessaggioByUtenteStatement = "SELECT * FROM Messaggio WHERE utenteOne = ? AND utenteTwo = ?";
		getLastMessaggioStatement = "SELECT * FROM get_last_n_messaggio(?, ?, ?)";
		checkIfChatIsUpToDateStatement = "SELECT messaggioCount FROM Chat WHERE utenteOne = ? AND utenteTwo = ?";
		getAllMessaggioStatement = "SELECT * FROM Messaggio WHERE utenteOne = ? AND utenteTwo = ?";
		getAllChatWithUtenteStatement = "SELECT * FROM Chat WHERE utenteOne = ? OR utenteTwo = ?";
	}
	
	@PostConstruct
	private void initJdbcTemplate() {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private final static class ChatMapper implements RowMapper<Chat>{
		
		public Chat mapRow(ResultSet rs, int rowNum) throws SQLException {
			Chat chat = new Chat();
			chat.setUtenteOneId(rs.getString("utenteOne"));
			chat.setUtenteTwoId(rs.getString("utenteTwo"));
			return chat;
		}
	}
	
	private final static class MessaggioMapper implements RowMapper<Messaggio>{
		
		public Messaggio mapRow(ResultSet rs, int rowNum) throws SQLException{
			Messaggio messaggio = new Messaggio();
			messaggio.setId(rs.getLong("id"));
			messaggio.setTesto(rs.getString("testo"));
			messaggio.setTimestamp(rs.getObject("ts", OffsetDateTime.class));
			messaggio.setUtenteOneSender(rs.getBoolean("isUtenteOneSender"));
			return messaggio;
		}
	}

	@Override
	public void insertChat(Chat chat) throws WrappedCRUDException {
		try {
			jdbcTemplate.update(insertChatStatement,
					chat.getUtenteOneId(),
					chat.getUtenteTwoId());
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}

	@Override
	public long insertMessaggio(Chat chat, Messaggio messaggio) throws WrappedCRUDException {
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException{
					PreparedStatement ps = 
							connection.prepareStatement(insertMessaggioStatement, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, chat.getUtenteOneId());
					ps.setString(2, chat.getUtenteTwoId());
					ps.setString(3, messaggio.getTesto());
					ps.setObject(4, messaggio.getTimestamp());
					ps.setBoolean(5, messaggio.isUtenteOneSender());
					return ps;
				}
		}, keyHolder);
			return keyHolder.getKey().longValue();
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
		
	}

	@Override
	public Chat getChatByUtente(Utente utenteOne, Utente utenteTwo) throws WrappedCRUDException {
		try {
			Chat chat;
			List<Chat> result = jdbcTemplate.query(getChatByUtenteStatement, new ChatMapper(),
					utenteOne.getEmail(),
					utenteTwo.getEmail());
			if (result.isEmpty()) return null;
			else chat = result.get(0);
			chat.addMessaggio(getMessaggioByUtente(utenteOne, utenteTwo));
			return chat;
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}
	
	private List<Messaggio> getMessaggioByUtente(Utente utenteOne, Utente utenteTwo) throws WrappedCRUDException {
		try {
			return jdbcTemplate.query(getMessaggioByUtenteStatement, new MessaggioMapper(), 
					utenteOne.getEmail(),
					utenteTwo.getEmail());
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}
	
	@Override
	public List<Messaggio> getLastMessaggio(Chat chat, long n) throws WrappedCRUDException {
		try {
			return jdbcTemplate.query(getLastMessaggioStatement, new MessaggioMapper(),
					chat.getUtenteOneId(),
					chat.getUtenteTwoId(),
					n);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}

	@Override
	public long checkIfChatIsUpToDate(String utenteOneId, String utenteTwoId, long currentNumberOfMessaggio) 
			throws WrappedCRUDException {
		try {
			return currentNumberOfMessaggio - 
					jdbcTemplate.queryForObject(checkIfChatIsUpToDateStatement, Long.class, utenteOneId, utenteTwoId);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}
	
	@Override
	public List<Messaggio> getAllMessaggio(String utenteOneId, String utenteTwoId) throws WrappedCRUDException {
		try {
			return jdbcTemplate.query(getAllMessaggioStatement, new MessaggioMapper(),
					utenteOneId,
					utenteTwoId);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}
	
	@Override
	public List<Chat> getAllChatWithUtente(String utenteId) throws WrappedCRUDException {
		try {
			return jdbcTemplate.query(getAllChatWithUtenteStatement, new ChatMapper(),
					utenteId, utenteId);
		}
		catch (DataAccessException dae) {
			throw new WrappedCRUDException(dae);
		}
	}

	@Override
	public Messaggio getMessaggioByChatMessaggioPosition(Chat chat, long messagePosition) throws WrappedCRUDException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteChat(Chat chat) throws WrappedCRUDException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteMessaggio(Chat chat, Messaggio messaggio) throws WrappedCRUDException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Messaggio> getMessaggioInRange(Chat chat, long startRange, long endRange) throws WrappedCRUDException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateMessaggio(Chat chat, Messaggio messaggio) throws WrappedCRUDException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Messaggio> getMissingMessaggio(Chat chat, long currentNumberOfMessaggio) throws WrappedCRUDException {
		// TODO Auto-generated method stub
		return null;
	}

}
