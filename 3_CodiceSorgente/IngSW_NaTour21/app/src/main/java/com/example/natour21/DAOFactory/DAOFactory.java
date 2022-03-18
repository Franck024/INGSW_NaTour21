package com.example.natour21.DAOFactory;

import com.example.natour21.DAOHTTP.*;
import com.example.natour21.DAOs.*;
import com.example.natour21.enums.ConnectionType;
import com.example.natour21.enums.EndPoint;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;

public class DAOFactory {

    private EndPoint endPoint;
    private ConnectionType connectionType;

    private String localHostBaseUrl = "";
    private String defaultConnectionSettingsErrorMessage;

    public DAOFactory(EndPoint endPoint, ConnectionType connectionType){
        this.endPoint = endPoint;
        this.connectionType = connectionType;
        defaultConnectionSettingsErrorMessage =
                "Invalid connection settings; endpoint: " + endPoint + " connectiontype: " + connectionType;
    }

    public DAOUtente getDAOUtente() throws InvalidConnectionSettingsException{
        if (endPoint == EndPoint.LOCAL && connectionType == ConnectionType.HTTP){
            return new DAOHTTPUtente(localHostBaseUrl);
        }
        else throw new InvalidConnectionSettingsException
                (defaultConnectionSettingsErrorMessage);
    }

    public DAOChat getDAOChat() throws InvalidConnectionSettingsException{
        if (endPoint == EndPoint.LOCAL && connectionType == ConnectionType.HTTP){
            return new DAOHTTPChat(localHostBaseUrl);
        }
        else throw new InvalidConnectionSettingsException
                (defaultConnectionSettingsErrorMessage);
    }

    public DAOItinerario getDAOItinerario() throws InvalidConnectionSettingsException{
        if (endPoint == EndPoint.LOCAL && connectionType == ConnectionType.HTTP){
            return new DAOHTTPItinerario(localHostBaseUrl);
        }
        else throw new InvalidConnectionSettingsException
                (defaultConnectionSettingsErrorMessage);
    }

    public DAOSegnalazione getDAOSegnalazione() throws InvalidConnectionSettingsException{
        if (endPoint == EndPoint.LOCAL && connectionType == ConnectionType.HTTP){
            return new DAOHTTPSegnalazione(localHostBaseUrl);
        }
        else throw new InvalidConnectionSettingsException
                (defaultConnectionSettingsErrorMessage);
    }


}
