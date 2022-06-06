package com.example.natour21.chat.views.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.natour21.chat.room.repositories.ChatRepository;
import com.example.natour21.chat.room.entities.ChatDBEntity;

import java.util.List;

public class ViewModelChat extends AndroidViewModel {

    private ChatRepository chatRepository;
    private final LiveData<List<ChatDBEntity>> chats;

    public ViewModelChat(Application application){
        super(application);
        chatRepository = new ChatRepository(application);
        chats = chatRepository.getAllChats();
    }

    public LiveData<List<ChatDBEntity>> getChats(){
        return chats;
    }

    public void insert(ChatDBEntity chat){
        chatRepository.insert(chat);
    }

}
