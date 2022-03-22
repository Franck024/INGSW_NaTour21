package com.example.natour21.chat.stompclient;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.natour21.DTO.DTOMessaggio;
import com.example.natour21.DTO.DTOMessaggioInsertResponse;
import com.example.natour21.entities.Messaggio;
import com.example.natour21.entities.Utente;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Observable;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class UserStompClient extends Observable {

    private StompClient stompClient;
    private final String BASE_URL = "";
    private final String ENDPOINT = "websocket-endpoint";
    private final String USER_ID;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());



    public UserStompClient(Utente utente){

       stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,
                "ws://" + BASE_URL + "/" + ENDPOINT + "/websocket");
       USER_ID = utente.getEmail();
    }

    @SuppressLint("CheckResult")
    public void connect(){
        stompClient.connect();
        stompClient.topic("/chat/messaggio/live/" + USER_ID)
                .subscribe(topicMessage -> {
                    setChanged();
                    Object obj;
                    try{
                        obj = objectMapper.readValue(topicMessage.getPayload(), DTOMessaggio.class);
                    }
                    catch (Exception firstException){
                        try{
                            obj = objectMapper.readValue(topicMessage.getPayload(), DTOMessaggioInsertResponse.class);
                        }
                        catch (Exception secondException)
                        {
                            obj = secondException;
                        }
                    }
                    notifyObservers(obj);
        });
    }
    public void disconnect(){
        stompClient.disconnect();
    }

    public void send(String receiver, Messaggio messaggio) throws JsonProcessingException {
        stompClient.send("/chat/messaggio/live/" + USER_ID + "/" + receiver,
                objectMapper.writeValueAsString(messaggio)).subscribe();
    }
}
