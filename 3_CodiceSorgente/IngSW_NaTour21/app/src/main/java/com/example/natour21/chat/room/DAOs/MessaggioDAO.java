package com.example.natour21.chat.room.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.natour21.chat.room.entities.MessaggioDBEntity;

import java.util.List;

@Dao
public interface MessaggioDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void insert(MessaggioDBEntity messaggioDBEntity);

    @Query("SELECT * FROM Messaggio ORDER BY id ASC")
    public LiveData<List<MessaggioDBEntity>> getAllMessaggio();

    @Query("SELECT * FROM Messaggio AS M WHERE M.otherUserId = :otherUserId")
    public LiveData<List<MessaggioDBEntity>> getAllMessaggioWithUser(String otherUserId);

    @Query("UPDATE Chat SET unreadMessageCount = 0 WHERE otherUserId = :otherUserId")
    void setChatRead(String otherUserId);
}
