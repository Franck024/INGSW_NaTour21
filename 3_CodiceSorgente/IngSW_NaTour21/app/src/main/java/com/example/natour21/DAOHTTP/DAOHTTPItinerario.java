package com.example.natour21.DAOHTTP;

import com.example.natour21.DAOs.DAOItinerario;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.entities.Utente;
import com.example.natour21.exceptions.WrappedCRUDException;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class DAOHTTPItinerario implements DAOItinerario {

    private interface HTTPAPIItinerario{
        @POST("/itinerario")
        public Call<Boolean> insertItinerario(@Body Itinerario itinerario);

        @GET("/itinerario")
        public Call<List<Itinerario>> getItinerario
                (
                    @Query("id") Long id,
                    @Query("numberToGet") Integer numberToGet,
                    @Query("idToStartFrom") Long idToStartFrom,
                    @Query("newestId") Long newestId,
                    @Query("utenteId") String utenteId
                );
    }

    HTTPAPIItinerario APIItinerario;

    public DAOHTTPItinerario(String baseUrl){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        APIItinerario = retrofit.create(HTTPAPIItinerario.class);
    }

    @Override
    public boolean insertItinerario(Itinerario itinerario) throws WrappedCRUDException {
        try{
            Response<Boolean> response = APIItinerario
                    .insertItinerario(itinerario)
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch (IOException ioe) {
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public Itinerario getItinerarioById(long idItinerario) throws WrappedCRUDException {
        try{
            Response<List<Itinerario>> response = APIItinerario
                    .getItinerario(idItinerario, null, null, null, null)
                    .execute();
            List<Itinerario> listItinerario = DAOHTTPUtil.handleResponse(response);
            if (listItinerario.isEmpty()) return null;
            else return listItinerario.get(0);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public List<Itinerario> getLastNItinerario(int n) throws WrappedCRUDException {
        try{
            Response<List<Itinerario>> response = APIItinerario
                    .getItinerario(null, n, null, null, null)
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public List<Itinerario> getLastNItinerarioStartingFrom(long startingFrom, int n) throws WrappedCRUDException {
        try{
            Response<List<Itinerario>> response = APIItinerario
                    .getItinerario(null, n, startingFrom, null, null)
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public List<Itinerario> getLastNItinerarioNewerThan(long newestId, int n) throws WrappedCRUDException {
        try{
            Response<List<Itinerario>> response = APIItinerario
                    .getItinerario(null, n, null, newestId, null)
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public List<Itinerario> getItinerarioByUtenteId(String utenteId) throws WrappedCRUDException {
        try{
            Response<List<Itinerario>> response = APIItinerario
                    .getItinerario(null, null, null, null, utenteId)
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public void deleteItinerario(Itinerario itinerario) throws WrappedCRUDException {

    }

    @Override
    public void updateItinerario(Itinerario itinerario) throws WrappedCRUDException {

    }

    @Override
    public List<Itinerario> getItinerarioByNome(String nomeItinerario) throws WrappedCRUDException {
        return null;
    }

    @Override
    public List<Itinerario> getItinerarioByPuntoIniziale(GeoPoint puntoIniziale) throws WrappedCRUDException {
        return null;
    }

}
