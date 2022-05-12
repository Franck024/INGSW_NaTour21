package com.example.natour21.chat.room.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.natour21.chat.room.entities.ChatDBEntity;

import java.util.List;

@Dao
public interface ChatDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(ChatDBEntity chat);

    @Query("SELECT * FROM Chat")
    LiveData<List<ChatDBEntity>> getAllChats();

    @Query("SELECT * FROM Chat")
    List<ChatDBEntity> getAllChatsSync();

    @Query("UPDATE Chat SET unreadMessageCount = 0 WHERE otherUserId = :otherUserId")
    void setChatRead(String otherUserId);

    @Query("SELECT EXISTS(SELECT * FROM Chat WHERE Chat.otherUserId = :userId)")
    boolean doesChatWithUserExist(String userId);

    @Query("SELECT SUM(unreadMessageCount) FROM Chat")
    long getTotalUnreadMessageCount();


}
