package com.example.natour21.DAOHTTPs;

import com.example.natour21.DAOs.DAOSegnalazione;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.entities.Segnalazione;
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

public class DAOHTTPSegnalazione implements DAOSegnalazione {

    private interface HTTPAPISegnalazione{
        @POST("/segnalazione")
        public Call<Boolean> insertSegnalazione(@Body Segnalazione segnalazione);

        @GET("/segnalazione")
        public Call<List<Segnalazione>> getSegnalazione(@Query("itinerarioId") long itinerarioId);

    }

    HTTPAPISegnalazione APISegnalazione;

    public DAOHTTPSegnalazione(String baseUrl){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        APISegnalazione = retrofit.create(HTTPAPISegnalazione.class);
    }

    @Override
    public boolean insertSegnalazione(Segnalazione segnalazione) throws WrappedCRUDException {
        try{
            Response<Boolean> response = APISegnalazione
                    .insertSegnalazione(segnalazione)
                    .execute();
            return DAOHTTPUtils.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public List<Segnalazione> getSegnalazioneByItinerario(Itinerario itinerario) throws WrappedCRUDException {
       try{
           Response<List<Segnalazione>> response = APISegnalazione
                   .getSegnalazione(itinerario.getId())
                   .execute();
           return DAOHTTPUtils.handleResponse(response);
       }
       catch (IOException ioe){
           throw new WrappedCRUDException(ioe);
       }
    }

    @Override
    public void deleteSegnalazione(Segnalazione segnalazione) throws WrappedCRUDException {

    }

    @Override
    public void updateSegnalazione(Segnalazione segnalazione) throws WrappedCRUDException {

    }




}
