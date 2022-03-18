package com.example.natour21.DAOHTTP;

import com.example.natour21.DAOs.DAOChat;
import com.example.natour21.entities.Chat;
import com.example.natour21.entities.Messaggio;
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

public class DAOHTTPChat implements DAOChat {
    private interface HTTPAPIChat {
        @POST("/chat/messaggio")
        public Call<Long> insertMessaggio
                (
                        @Body Messaggio messaggio,
                        @Query("utenteOneId") String utenteOneId,
                        @Query("utenteTwoId") String utenteTwoId
                );
        @GET("/chat/messaggio")
        public Call<List<Messaggio>> getMessaggio
                (
                        @Query("utenteOneId") String utenteOneId,
                        @Query("utenteTwoId") String utenteTwoId,
                        @Query("numberOfMessaggioToGet") Integer numberOfMessaggioToGet,
                        @Query("currentNumberOfMessaggio") Integer currentNumberOfMessaggio,
                        @Query("getAllMessaggio") Boolean getAllMessaggio
                );
        @POST("/chat")
        public Call<Boolean> insertChat
                (
                        @Query("utenteOneId") String utenteOneId,
                        @Query("utenteTwoId") String utenteTwoId
                );

        @GET("/chat")
        public Call<Chat> getChat
                (
                        @Query("utenteOneId") String utenteOneId,
                        @Query("utenteTwoId") String utenteTwoId
                );
    }

    HTTPAPIChat APIChat;

    public DAOHTTPChat(String baseUrl){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        APIChat = retrofit.create(HTTPAPIChat.class);
    }

    @Override
    public boolean insertChat(Chat chat) throws WrappedCRUDException {
        try{
            Response<Boolean> response = APIChat
                    .insertChat
                            (
                                    chat.getUtenteOneId(),
                                    chat.getUtenteTwoId()
                            )
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public long insertMessaggio(Chat chat, Messaggio messaggio) throws WrappedCRUDException {
        try{
            Response<Long> response = APIChat
                    .insertMessaggio
                    (
                            messaggio,
                            chat.getUtenteOneId(),
                            chat.getUtenteTwoId()
                    )
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public Chat getChatByUtente(Utente utenteOne, Utente utenteTwo) throws WrappedCRUDException {
        try{
            Response<Chat> response = APIChat
                    .getChat
                            (
                                    utenteOne.getEmail(),
                                    utenteTwo.getEmail()
                            )
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    public List<Messaggio> getMissingMessaggio(Chat chat) throws WrappedCRUDException{
        try{
            Response<List<Messaggio>> response = APIChat
                    .getMessaggio
                            (
                                    chat.getUtenteOneId(),
                                    chat.getUtenteTwoId(),
                                    null,
                                    chat.getNumberOfMessaggio(),
                                    null
                            )
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }
    @Override
    public List<Messaggio> getAllMessaggio(String utenteOneId, String utenteTwoId) throws WrappedCRUDException {
        try{
            Response<List<Messaggio>> response = APIChat
                    .getMessaggio
                            (
                                    utenteOneId,
                                    utenteTwoId,
                                    null,
                                    null,
                                    true
                            )
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch (IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public List<Messaggio> getLastMessaggio(Chat chat, int n) throws WrappedCRUDException {
        try{
            Response<List<Messaggio>> response = APIChat
                    .getMessaggio
                            (
                                    chat.getUtenteOneId(),
                                    chat.getUtenteTwoId(),
                                    n,
                                    null,
                                    null
                            )
                    .execute();
            return DAOHTTPUtil.handleResponse(response);
        }
        catch(IOException ioe){
            throw new WrappedCRUDException(ioe);
        }
    }

    @Override
    public Messaggio getMessaggioByChatMessaggioPosition(Chat chat, int messagePosition) throws WrappedCRUDException {
        return null;
    }

    @Override
    public void deleteChat(Chat chat) throws WrappedCRUDException {

    }

    @Override
    public void deleteMessaggio(Chat chat, Messaggio messaggio) throws WrappedCRUDException {

    }

    @Override
    public List<Messaggio> getMessaggioInRange(Chat chat, int startRange, int endRange) throws WrappedCRUDException {
        return null;
    }

    @Override
    public void updateMessaggio(Chat chat, Messaggio messaggio) throws WrappedCRUDException {

    }

    @Override
    public int checkIfChatIsUpToDate(String utenteOneId, String utenteTwoId, int currentNumberOfMessaggio) throws WrappedCRUDException {
        return 0;
    }
}
