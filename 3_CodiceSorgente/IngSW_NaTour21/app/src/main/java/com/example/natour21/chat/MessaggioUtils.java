package com.example.natour21.chat;

import com.example.natour21.DTOs.DTOMessaggio;
import com.example.natour21.DTOs.DTOMessaggioInsertResponse;
import com.example.natour21.chat.room.entities.MessaggioDBEntity;
import com.example.natour21.entities.Messaggio;

import java.time.OffsetDateTime;

public interface MessaggioUtils {

    public static MessaggioDBEntity convertToMessaggioDBEntity(DTOMessaggio dtoMessaggio){
        String sender = dtoMessaggio.getSender();
        Messaggio messaggio = dtoMessaggio.getMessaggio();
        long id = messaggio.getId();
        String testo = messaggio.getTesto();
        OffsetDateTime timestamp = messaggio.getTimestamp();
        return new MessaggioDBEntity
                (id, sender, testo, timestamp, false);
    }

    public static  MessaggioDBEntity convertToMessaggioDBEntity
            (DTOMessaggioInsertResponse dtoMessaggioInsertResponse,
             Messaggio messaggio, String otherUserId){
        long id = dtoMessaggioInsertResponse.getId();
        String testo = messaggio.getTesto();
        OffsetDateTime timestamp = messaggio.getTimestamp();
        return new MessaggioDBEntity
                (id, otherUserId, testo, timestamp, true);
    }
}
