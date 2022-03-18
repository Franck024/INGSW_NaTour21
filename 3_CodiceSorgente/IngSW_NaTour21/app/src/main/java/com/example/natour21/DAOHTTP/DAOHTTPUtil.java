package com.example.natour21.DAOHTTP;

import com.example.natour21.exceptions.WrappedCRUDException;

import java.io.IOException;

import retrofit2.Response;

public class DAOHTTPUtil {
    public static <T extends Object>T handleResponse(Response<T> response) throws WrappedCRUDException{
        try{
            if (response.isSuccessful()) return response.body();
            else throw new WrappedCRUDException(new Exception
                    (response.errorBody().string()));
        }
        catch (IOException ioe) {
            throw new WrappedCRUDException(ioe);
        }
    }
}
