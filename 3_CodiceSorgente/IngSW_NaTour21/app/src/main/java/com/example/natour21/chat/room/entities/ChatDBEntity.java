package com.example.natour21.chat.room.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Chat")
public class ChatDBEntity {

    @PrimaryKey
    @NonNull
    private String otherUserId;

    @NonNull
    private String nomeChat;

    @NonNull
    private long messageCount;

    @NonNull
    private long unreadMessageCount;

    @NonNull
    private boolean isCurrentUtenteUtenteOneInBackend;

    public ChatDBEntity(String otherUserId, String nomeChat, boolean isCurrentUtenteUtenteOneInBackend ){
        this.otherUserId = otherUserId;
        this.nomeChat = nomeChat;
        this.isCurrentUtenteUtenteOneInBackend = isCurrentUtenteUtenteOneInBackend;
        messageCount = 0;
        unreadMessageCount = 0;
    }

    public void setOtherUserId(@NonNull String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public void setNomeChat(@NonNull String nomeChat) {
        this.nomeChat = nomeChat;
    }

    public void setMessageCount(long messageCount) {
        this.messageCount = messageCount;
    }

    public void setUnreadMessageCount(long unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public void setCurrentUtenteUtenteOneInBackend(boolean currentUtenteUtenteOneInBackend) {
        isCurrentUtenteUtenteOneInBackend = currentUtenteUtenteOneInBackend;
    }

    @NonNull
    public String getOtherUserId(){ return otherUserId;}

    @NonNull
    public String getNomeChat() {
        return nomeChat;
    }

    @NonNull
    public long getMessageCount() { return messageCount;}

    @NonNull
    public long getUnreadMessageCount(){ return unreadMessageCount;}

    @NonNull
    public boolean isCurrentUtenteUtenteOneInBackend() { return isCurrentUtenteUtenteOneInBackend;}

    public void setRead() {unreadMessageCount = 0;}
}
