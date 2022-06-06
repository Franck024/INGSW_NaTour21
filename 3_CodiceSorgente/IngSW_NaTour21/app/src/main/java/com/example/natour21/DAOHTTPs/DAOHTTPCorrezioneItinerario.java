package com.example.natour21.DAOHTTPs;

import com.example.natour21.DAOs.DAOCorrezioneItinerario;
import com.example.natour21.entities.CorrezioneItinerario;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.exceptions.WrappedCRUDException;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class DAOHTTPCorrezioneItinerario implements DAOCorrezioneItinerario {

    private interface HTTPAPICorrezioneItinerario{
        @POST("/correzioneItinerario")
        public Call<Boolean> insertCorrezioneItinerario
                (
                        @Query("correzioneItinerario") CorrezioneItinerario correzioneItinerario
                );

        @GET("/correzioneItinerario")
        public Call<List<CorrezioneItinerario>> getCorrezioneItinerario
                (
                        @Query("itinerarioId") long itinerarioId
                );
    }

    HTTPAPICorrezioneItinerario APICorrezioneItinerario;

    public DAOHTTPCorrezioneItinerario(String baseUrl){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        APICorrezioneItinerario = retrofit.create(DAOHTTPCorrezioneItinerario.HTTPAPICorrezioneItinerario.class);
    }

    @Override
    public boolean insertCorrezioneItinerario(CorrezioneItinerario correzioneItinerario) throws WrappedCRUDException {
        try{
            Response<Boolean> response = APICorrezioneItinerario
                    .insertCorrezioneItinerario(correzioneItinerario)
                    .execute();
            return DAOHTTPUtils.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public List<CorrezioneItinerario> getCorrezioneItinerarioByItinerario(Itinerario itinerario) throws WrappedCRUDException {
        try{
            Response<List<CorrezioneItinerario>> response = APICorrezioneItinerario
                    .getCorrezioneItinerario(itinerario.getId())
                    .execute();
            return DAOHTTPUtils.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public void deleteCorrezioneItinerario(CorrezioneItinerario correzioneItinerario) throws WrappedCRUDException {

    }

    @Override
    public void updateCorrezioneItinerario(CorrezioneItinerario correzioneItinerario) throws WrappedCRUDException {

    }




}
