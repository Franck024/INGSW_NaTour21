package com.example.natour21.DAOFactory;

import com.example.natour21.DAOHTTP.*;
import com.example.natour21.DAOs.*;
import com.example.natour21.enums.ConnectionType;
import com.example.natour21.enums.EndPoint;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;

public class DAOFactory {

    private EndPoint endPoint;
    private ConnectionType connectionType;

    private String EC2BaseUrl = "";
    private String localHostBaseUrl = "";
    private String defaultConnectionSettingsErrorMessage;

    public DAOFactory(EndPoint endPoint, ConnectionType connectionType){
        this.endPoint = endPoint;
        this.connectionType = connectionType;
        defaultConnectionSettingsErrorMessage =
                "Invalid connection settings; endpoint: " + endPoint + " connectiontype: " + connectionType;
    }

    public DAOUtente getDAOUtente() throws InvalidConnectionSettingsException{
        if (connectionType.equals(ConnectionType.HTTP)){
            if (endPoint.equals(EndPoint.LOCAL)) return new DAOHTTPUtente(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPUtente(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (defaultConnectionSettingsErrorMessage);
    }

    public DAOChat getDAOChat() throws InvalidConnectionSettingsException{
        if (connectionType.equals(ConnectionType.HTTP)){
            if (endPoint.equals(EndPoint.LOCAL)) return new DAOHTTPChat(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPChat(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (defaultConnectionSettingsErrorMessage);
    }

    public DAOItinerario getDAOItinerario() throws InvalidConnectionSettingsException{
        if (connectionType.equals(ConnectionType.HTTP)){
            if (endPoint.equals(EndPoint.LOCAL)) return new DAOHTTPItinerario(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPItinerario(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (defaultConnectionSettingsErrorMessage);
    }

    public DAOSegnalazione getDAOSegnalazione() throws InvalidConnectionSettingsException {
        if (connectionType.equals(ConnectionType.HTTP)) {
            if (endPoint.equals(EndPoint.LOCAL)) return new DAOHTTPSegnalazione(localHostBaseUrl);
            else if (endPoint.equals(EndPoint.EC2)) return new DAOHTTPSegnalazione(EC2BaseUrl);
        }
        throw new InvalidConnectionSettingsException
                (defaultConnectionSettingsErrorMessage);
    }
}
