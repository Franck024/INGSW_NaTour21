package com.example.natour21.chat.room.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.natour21.chat.room.entities.ChatDBEntity;
import com.example.natour21.chat.room.ChatDatabase;
import com.example.natour21.chat.room.DAOs.*;

import java.util.List;

public class ChatRepository {

    private ChatDAO chatDAO;
    private LiveData<List<ChatDBEntity>> allChats;

    public ChatRepository(Application application) {
        ChatDatabase db = ChatDatabase.getDatabase(application);
        chatDAO = db.chatDAO();
        allChats = chatDAO.getAllChats();
    }

    public LiveData<List<ChatDBEntity>> getAllChats() {
        return allChats;
    }

    public List<ChatDBEntity> getAllChatsNonLiveDataBlocking() {
        return chatDAO.getAllChatsSync();
    }


    public void insert(ChatDBEntity chat) {
        ChatDatabase.getExecutorService().execute(() -> {
            chatDAO.insert(chat);
        });
    }

    public void setRead(ChatDBEntity chat){
        ChatDatabase.getExecutorService().execute(() -> {
            chatDAO.setChatRead(chat.getOtherUserId());
        });
    }

    public void insertBlocking(ChatDBEntity chat){
        chatDAO.insert(chat);
    }

    public boolean doesChatWithUserExistBlocking(String userId){
        return chatDAO.doesChatWithUserExist(userId);
    }

    public long getUnreadMessageCountBlocking(){
        return chatDAO.getTotalUnreadMessageCount();
    }


}

