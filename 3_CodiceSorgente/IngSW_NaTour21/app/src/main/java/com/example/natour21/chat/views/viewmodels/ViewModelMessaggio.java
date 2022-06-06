package com.example.natour21.chat.views.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.natour21.chat.room.entities.MessaggioDBEntity;
import com.example.natour21.chat.room.repositories.MessaggioRepository;

import java.util.List;

public class ViewModelMessaggio extends AndroidViewModel {

    private MessaggioRepository messaggioRepository;

    private final LiveData<List<MessaggioDBEntity>> messaggi;

    private final String otherUserId;

    public ViewModelMessaggio(Application application, String otherUserId){
        super(application);
        messaggioRepository = new MessaggioRepository(application, otherUserId);
        this.otherUserId = otherUserId;
        messaggi = messaggioRepository.getAllMessaggio();
    }

    public LiveData<List<MessaggioDBEntity>> getAllMessaggi() { return messaggi;}

    public void insert(MessaggioDBEntity messaggioDBEntity){
        messaggioRepository.insert(messaggioDBEntity);
    }

    public void setChatRead(String otherUserId){
        messaggioRepository.setChatRead(otherUserId);
    }


}
