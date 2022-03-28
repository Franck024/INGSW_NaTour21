package com.example.natour21.DAOFactory;

import com.example.natour21.DAOHTTP.*;
import com.example.natour21.DAOs.*;
import com.example.natour21.enums.ConnectionType;
import com.example.natour21.enums.EndPoint;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;

public class DAOFactory {

    private static EndPoint endPoint = EndPoint.LOCAL;
    private static ConnectionType connectionType = ConnectionType.HTTP;

    private static String EC2BaseUrl = "";
    private static String localHostBaseUrl = "http://192.168.1.220:5000";

    private static String getDefaultConnectionSettingsErrorMessage(){
        return "Invalid connection settings; endpoint: " + endPoint + " connectiontype: " + connectionType;
    }

    public static DAOUtente getDAOUtente() throws InvalidConnectionSettingsException{
        if (connectionType.equals(ConnectionType.HTTP)){
            if (endPoint.equals(EndPoint.LOCAL)) return new DAOHTTPUtente(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPUtente(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (getDefaultConnectionSettingsErrorMessage());
    }

    public static DAOChat getDAOChat() throws InvalidConnectionSettingsException{
        if (connectionType.equals(ConnectionType.HTTP)){
            if (endPoint.equals(EndPoint.LOCAL)) return new DAOHTTPChat(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPChat(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (getDefaultConnectionSettingsErrorMessage());
    }

    public static DAOItinerario getDAOItinerario() throws InvalidConnectionSettingsException{
        if (connectionType.equals(ConnectionType.HTTP)){
            if (endPoint.equals(EndPoint.LOCAL)) return new DAOHTTPItinerario(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPItinerario(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (getDefaultConnectionSettingsErrorMessage());
    }

    public static DAOSegnalazione getDAOSegnalazione() throws InvalidConnectionSettingsException {
        if (connectionType.equals(ConnectionType.HTTP)) {
            if (endPoint.equals(EndPoint.LOCAL)) return new DAOHTTPSegnalazione(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPSegnalazione(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (getDefaultConnectionSettingsErrorMessage());
    }

    public static DAOStatistiche getDAOStatistiche() throws InvalidConnectionSettingsException {
        if (connectionType.equals(ConnectionType.HTTP)) {
            if (endPoint.equals(EndPoint.LOCAL)) return new DAOHTTPStatistiche(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPStatistiche(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (getDefaultConnectionSettingsErrorMessage());
    }
}
