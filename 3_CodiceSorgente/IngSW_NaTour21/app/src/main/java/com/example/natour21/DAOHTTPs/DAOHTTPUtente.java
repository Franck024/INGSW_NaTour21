package com.example.natour21.DAOHTTP;

import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.entities.Utente;
import com.example.natour21.exceptions.WrappedCRUDException;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class DAOHTTPUtente implements DAOUtente {

    private interface HTTPAPIUtente {

        @GET("/utente")
        Call<List<Utente>> getUtente
                (
                        @Query("email") String email,
                        @Query("citta") String citta
                );

        @POST("/utente")
        Call<Boolean> insertUtente(@Body Utente utente);
    }

    HTTPAPIUtente APIUtente;

    public DAOHTTPUtente(String baseUrl){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        APIUtente = retrofit.create(HTTPAPIUtente.class);
    }

    @Override
    public boolean insertUtente(Utente utente) throws WrappedCRUDException {
        try{
            Call<Boolean> call = APIUtente.insertUtente(utente);
            Response<Boolean> response = call.execute();
            return DAOHTTPUtils.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public Utente getUtenteByEmail(String email) throws WrappedCRUDException {
        try{
            Response<List<Utente>> response = APIUtente
                    .getUtente(email, null)
                    .execute();
            List<Utente> listUtente = DAOHTTPUtils.handleResponse(response);
            if (listUtente.isEmpty()) return null;
            else return listUtente.get(0);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public void deleteUtente(Utente utente) throws WrappedCRUDException {

    }

    @Override
    public void updateUtente(Utente utente) throws WrappedCRUDException {

    }

    @Override
    public List<Utente> getUtenteByCitta(String citta) throws WrappedCRUDException {
        return null;
    }


}
