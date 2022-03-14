package org.ingsw21.backend.DAOFactories;


import java.sql.SQLException;

import org.ingsw21.backend.DAOSQL.*;
import org.ingsw21.backend.DAOs.*;
import org.ingsw21.backend.enums.DataSourceType;
import org.ingsw21.backend.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;


//Nota: bisogna rendere @Autowired in questa classe
//tutti i DAO che implementano concretamente i metodi delle loro interfacce.
@Component
@PropertySources({@PropertySource("classpath:connection.properties")})
public class DAOFactory {

	@Autowired
	DAOSQLUtente DAOSQLUtente;
	@Autowired
	DAOSQLSegnalazione DAOSQLSegnalazione;
	@Autowired
	DAOSQLChat DAOSQLChat;
	@Autowired
	DAOSQLItinerario DAOSQLItinerario;
	
	
	@Value("${ingsw21.connection.datasourcetype}")
	DataSourceType dataSourceType;
	
	public DAOUtente getDAOUtente() throws InvalidDataSourceException {
		if(dataSourceType.toString().contains("POSTGRE")) 
			return DAOSQLUtente;
		throw new InvalidDataSourceException("Invalid data source: " + dataSourceType.toString());
	}
	
	public DAOSegnalazione getDAOSegnalazione() throws InvalidDataSourceException{
		if(dataSourceType.toString().contains("POSTGRE")) 
			return DAOSQLSegnalazione;
		throw new InvalidDataSourceException("Invalid data source: " + dataSourceType.toString());
	}
	
	public DAOChat getDAOChat() throws InvalidDataSourceException{
		if(dataSourceType.toString().contains("POSTGRE")) 
			return DAOSQLChat;
		throw new InvalidDataSourceException("Invalid data source: " + dataSourceType.toString());
	}
	
	public DAOItinerario getDAOItinerario() throws InvalidDataSourceException{
		if(dataSourceType.toString().contains("POSTGRE")) 
			return DAOSQLItinerario;
		throw new InvalidDataSourceException("Invalid data source: " + dataSourceType.toString());
	}
}
