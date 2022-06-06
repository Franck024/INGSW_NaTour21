package com.example.natour21.configs;

import androidx.annotation.NonNull;

import com.example.natour21.enums.BLOBStorage;
import com.example.natour21.enums.ConnectionType;
import com.example.natour21.enums.EndPoint;

public class ConnectionConfig {
    private static final EndPoint endPoint = EndPoint.LOCAL_TESTING;
    private static final ConnectionType connectionType = ConnectionType.HTTP;

    private static final String EC2BaseUrl = "";
    private static final String localHostBaseUrl = "http://192.168.1.220:5000";

    private static final BLOBStorage blobStorage = BLOBStorage.S3;

    public static EndPoint getEndPoint() {
        return endPoint;
    }

    public static ConnectionType getConnectionType() {
        return connectionType;
    }

    public static String getEC2BaseUrl() {
        return EC2BaseUrl;
    }

    public static String getLocalHostBaseUrl() {
        return localHostBaseUrl;
    }

    public static BLOBStorage getBlobStorage() {
        return blobStorage;
    }



    public static String getConfigAsString(){
        return "endpoint: " + endPoint +
                "\nconnectiontype: " + connectionType
                + "\nblobstorage: " + blobStorage
                + "\nEC2BaseUrl: " + EC2BaseUrl
                + "\nlocalHostBaseUrl: " + localHostBaseUrl;
    }
}
