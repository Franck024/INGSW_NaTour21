package com.example.natour21.DAOHTTP;

import com.example.natour21.DAOs.DAOStatistiche;
import com.example.natour21.entities.Statistiche;
import com.example.natour21.exceptions.WrappedCRUDException;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class DAOHTTPStatistiche implements DAOStatistiche {

    private interface HTTPAPIStatistiche{
        @GET("/statistiche")
        public Call<Statistiche> getStatistiche();

        @POST("/statistiche")
        public Call<Boolean> incrementUtenteAccess();
    }

    HTTPAPIStatistiche APIStatistiche;

    public DAOHTTPStatistiche(String baseUrl){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        APIStatistiche = retrofit.create(HTTPAPIStatistiche.class);
    }

    @Override
    public boolean incrementUtenteAccess() throws WrappedCRUDException {
        try{
            Response<Boolean> response = APIStatistiche
                    .incrementUtenteAccess()
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch(IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public Statistiche getStatistiche() throws WrappedCRUDException {
        try{
            Response<Statistiche> response = APIStatistiche
                    .getStatistiche()
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }


}
