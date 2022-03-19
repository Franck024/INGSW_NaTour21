package com.example.natour21.chat.stompclient;

import android.util.Log;

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

    public void connect(){
        stompClient.connect();
        stompClient.topic("/chat/messaggio/live/" + USER_ID)
                .subscribe(topicMessage -> {
                    setChanged();
                    notifyObservers(topicMessage.getPayload());
        });
    }

    public void disconnect(){
        stompClient.disconnect();
    }

    public void send(String receiver, Messaggio messaggio) throws JsonProcessingException {
        stompClient.send("/chat/messaggio/live/" + USER_ID + "/" + receiver,
                objectMapper.writeValueAsString(messaggio));
    }
}
