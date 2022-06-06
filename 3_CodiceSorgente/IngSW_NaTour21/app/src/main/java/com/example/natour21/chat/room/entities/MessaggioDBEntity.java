package com.example.natour21.chat.room.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import java.time.OffsetDateTime;

@Entity(tableName = "Messaggio",
        primaryKeys = {"otherUserId", "id"},
        foreignKeys = {@ForeignKey(entity = ChatDBEntity.class,
                parentColumns = "otherUserId",
                childColumns = "otherUserId",
                onDelete = ForeignKey.CASCADE)
        })
public class MessaggioDBEntity {
    @NonNull
    private long id;

    @NonNull
    private String otherUserId;

    @NonNull
    private String text;

    @NonNull
    private OffsetDateTime timestamp;

    @NonNull
    private boolean isMainUtenteSender;

    public MessaggioDBEntity(long id, @NonNull String otherUserId, @NonNull String text, @NonNull OffsetDateTime timestamp,
                             boolean isMainUtenteSender) {
        this.id = id;
        this.otherUserId = otherUserId;
        this.text = text;
        this.timestamp = timestamp;
        this.isMainUtenteSender = isMainUtenteSender;
    }


    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(@NonNull String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }

    public void setTimestamp(@NonNull OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setMainUtenteSender(boolean mainUtenteSender) {
        isMainUtenteSender = mainUtenteSender;
    }

    public boolean isMainUtenteSender() {
        return isMainUtenteSender;
    }



    @NonNull
    public String getText() {
        return text;
    }

    @NonNull
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}
