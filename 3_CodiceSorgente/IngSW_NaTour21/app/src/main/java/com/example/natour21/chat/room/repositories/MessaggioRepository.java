package com.example.natour21.chat.room.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.natour21.chat.room.ChatDatabase;
import com.example.natour21.chat.room.DAOs.*;
import com.example.natour21.chat.room.entities.ChatDBEntity;
import com.example.natour21.chat.room.entities.MessaggioDBEntity;

import java.util.List;

public class MessaggioRepository {

    private MessaggioDAO messaggioDAO;
    private LiveData<List<MessaggioDBEntity>> allMessaggio;


    public MessaggioRepository(Application application, String otherUserId){
        ChatDatabase db = ChatDatabase.getDatabase(application);
        messaggioDAO = db.messaggioDAO();
        allMessaggio = messaggioDAO.getAllMessaggioWithUser(otherUserId);
    }

    public MessaggioRepository(Application application){
        ChatDatabase db = ChatDatabase.getDatabase(application);
        messaggioDAO = db.messaggioDAO();
    }

    public LiveData<List<MessaggioDBEntity>> getAllMessaggio(){
        return allMessaggio;
    }

    public void insert(MessaggioDBEntity messaggioDBEntity){
        ChatDatabase.getExecutorService().execute(() -> {
            messaggioDAO.insert(messaggioDBEntity);
        });
    }

    public void setChatRead(String otherUserId){
        ChatDatabase.getExecutorService().execute(() -> {
            messaggioDAO.setChatRead(otherUserId);
        });
    }

    public void insertBlocking(MessaggioDBEntity messaggioDBEntity){
        messaggioDAO.insert(messaggioDBEntity);
    }


}
