package org.ingsw21.backend.configurations;
import javax.sql.DataSource;

import org.ingsw21.backend.enums.DataSourceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@PropertySources({@PropertySource("classpath:connection.properties")})
@Configuration
public class JDBCDataSourceConfig {
	
	
	@Bean
	public DataSource getDataSource(@Value("${ingsw21.connection.datasourcetype}") DataSourceType dataSourceType) {
		
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
       
        String USERNAME;
		String PASSWORD; 
        String IP;
        String PORT;
        String url;
        String DBNAME;
        String DRIVERNAME;
        
        if (dataSourceType.equals(DataSourceType.POSTGRE_LOCAL)) {
        	USERNAME = "postgres";
			PASSWORD = "metanismo18";
			IP = "localhost";
			PORT = "5432"; 
			DBNAME = "INGSW21Second";
			url = "jdbc:postgresql://"+IP+":"+PORT+"/"+DBNAME;
			DRIVERNAME = "org.postgresql.Driver";
        }
        else return null;
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(USERNAME);
        dataSourceBuilder.password(PASSWORD);
        dataSourceBuilder.driverClassName(DRIVERNAME);
        return dataSourceBuilder.build();
	}
	

}
