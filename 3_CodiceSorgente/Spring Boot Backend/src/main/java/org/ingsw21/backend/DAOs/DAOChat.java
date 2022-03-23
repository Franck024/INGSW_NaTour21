package org.ingsw21.backend.DAOs;

import java.util.List;

import org.ingsw21.backend.entities.Chat;
import org.ingsw21.backend.entities.Messaggio;
import org.ingsw21.backend.entities.Utente;
import org.ingsw21.backend.exceptions.WrappedCRUDException;

public interface DAOChat {
	
	public void insertChat(Chat chat) throws WrappedCRUDException;
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
	public List<Chat> getAllChatWithUtente(String utenteId) throws WrappedCRUDException;
}
