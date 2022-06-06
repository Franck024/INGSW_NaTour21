package com.example.natour21.DAOFactory;

import com.example.natour21.DAOHTTPs.*;
import com.example.natour21.DAOs.*;
import com.example.natour21.configs.ConnectionConfig;
import com.example.natour21.enums.BLOBStorage;
import com.example.natour21.enums.ConnectionType;
import com.example.natour21.enums.EndPoint;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;

public class DAOFactory {

    private static final EndPoint endPoint = ConnectionConfig.getEndPoint();
    private static final ConnectionType connectionType = ConnectionConfig.getConnectionType();

    private static final String EC2BaseUrl = ConnectionConfig.getEC2BaseUrl();
    private static final String localHostBaseUrl = ConnectionConfig.getLocalHostBaseUrl();

    private static final BLOBStorage blobStorage = ConnectionConfig.getBlobStorage();

    private static String getDefaultConnectionSettingsErrorMessage(){
        return "Invalid connection settings: " + ConnectionConfig.getConfigAsString();
    }

    public static DAOUtente getDAOUtente() throws InvalidConnectionSettingsException{
        if (connectionType.equals(ConnectionType.HTTP)){
            if (endPoint.equals(EndPoint.LOCAL_TESTING)) return new DAOHTTPUtente(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPUtente(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (getDefaultConnectionSettingsErrorMessage());
    }

    public static DAOChat getDAOChat() throws InvalidConnectionSettingsException{
        if (connectionType.equals(ConnectionType.HTTP)){
            if (endPoint.equals(EndPoint.LOCAL_TESTING)) return new DAOHTTPChat(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPChat(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (getDefaultConnectionSettingsErrorMessage());
    }

    public static DAOItinerario getDAOItinerario() throws InvalidConnectionSettingsException{
        if (connectionType.equals(ConnectionType.HTTP)){
            if (endPoint.equals(EndPoint.LOCAL_TESTING)) return new DAOHTTPItinerario(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPItinerario(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (getDefaultConnectionSettingsErrorMessage());
    }

    public static DAOSegnalazione getDAOSegnalazione() throws InvalidConnectionSettingsException {
        if (connectionType.equals(ConnectionType.HTTP)) {
            if (endPoint.equals(EndPoint.LOCAL_TESTING)) return new DAOHTTPSegnalazione(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPSegnalazione(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (getDefaultConnectionSettingsErrorMessage());
    }

    public static DAOStatistiche getDAOStatistiche() throws InvalidConnectionSettingsException {
        if (connectionType.equals(ConnectionType.HTTP)) {
            if (endPoint.equals(EndPoint.LOCAL_TESTING)) return new DAOHTTPStatistiche(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPStatistiche(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (getDefaultConnectionSettingsErrorMessage());
    }

    public static DAOCorrezioneItinerario getDAOCorrezioneItinerario() throws InvalidConnectionSettingsException {
        if (connectionType.equals(ConnectionType.HTTP)) {
            if (endPoint.equals(EndPoint.LOCAL_TESTING)) return new DAOHTTPCorrezioneItinerario(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPCorrezioneItinerario(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (getDefaultConnectionSettingsErrorMessage());
    }

}
