package com.example.natour21.DAOs;

import com.example.natour21.entities.*;
import com.example.natour21.exceptions.WrappedCRUDException;
import java.util.List;

public interface DAOChat {

    public boolean insertChat(Chat chat) throws WrappedCRUDException;
    public long insertMessaggio(Chat chat, Messaggio messaggio) throws WrappedCRUDException;
    public Chat getChatByUtente(Utente utenteOne, Utente utenteTwo) throws WrappedCRUDException;
    public Messaggio getMessaggioByChatMessaggioPosition(Chat chat, int messagePosition) throws WrappedCRUDException;
    public void deleteChat(Chat chat) throws WrappedCRUDException;
    public void deleteMessaggio(Chat chat, Messaggio messaggio) throws WrappedCRUDException;
    public List<Messaggio> getMessaggioInRange(Chat chat, int startRange, int endRange) throws WrappedCRUDException;
    public void updateMessaggio(Chat chat, Messaggio messaggio) throws WrappedCRUDException;
    public List<Messaggio> getLastMessaggio(Chat chat, int n) throws WrappedCRUDException;
    public List<Messaggio> getAllMessaggio(String utenteOneId, String utenteTwoId) throws WrappedCRUDException;
    public int checkIfChatIsUpToDate(String utenteOneId, String utenteTwoId, int currentNumberOfMessaggio) throws WrappedCRUDException;

}