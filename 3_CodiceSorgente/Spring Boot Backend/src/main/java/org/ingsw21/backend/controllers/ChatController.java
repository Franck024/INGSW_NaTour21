package org.ingsw21.backend.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import org.ingsw21.backend.exceptions.*;
import org.ingsw21.backend.DAOFactories.DAOFactory;
import org.ingsw21.backend.DAOs.DAOChat;
import org.ingsw21.backend.DTO.DTOMessaggio;
import org.ingsw21.backend.DTO.DTOMessaggioInsertResponse;
import org.ingsw21.backend.entities.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
	
	@Autowired
	private DAOFactory DAOFactory;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	private DAOChat DAOChat;
	
	private enum ChatGetQuery{
		NUMBER_OF_MESSAGGIO_BEHIND,
		GET_ALL_MESSAGGIO,
		GET_LAST_N_MESSAGGIO
	}
	
	@PostMapping
	public boolean insertChat
	(
			@RequestParam String utenteOneId,
			@RequestParam String utenteTwoId
	) throws Exception
	{
		if (utenteOneId == null || utenteOneId.equals("") || utenteTwoId == null || utenteTwoId.equals(""))
			throw new BadRequestWebException();
		try {
			DAOChat = DAOFactory.getDAOChat();
			Chat inputChat = new Chat();
			inputChat.setUtenteOneId(utenteOneId);
			inputChat.setUtenteTwoId(utenteTwoId);
			DAOChat.insertChat(inputChat);
			return true;
		}
		catch (WrappedCRUDException wcrude) {
			throw (wcrude.getWrappedException());
		}
	}
	
	//Cerca una chat per due utenti, con entrambe le combinazioni.
	//Se la trova, da in output la combinazione ordinata com'è nel DB.
	//Altrimenti, da in output null
	@GetMapping
	public Chat getChat
	(
			@RequestParam String utenteOneId,
			@RequestParam String utenteTwoId
	) throws Exception
	{
		if (utenteOneId == null || utenteOneId.equals("") || utenteTwoId == null || utenteTwoId.equals(""))
			throw new BadRequestWebException();
		try {
			DAOChat = DAOFactory.getDAOChat();
			Utente inputUtenteOne = new Utente();
			Utente inputUtenteTwo = new Utente();
			inputUtenteOne.setEmail(utenteOneId);
			inputUtenteTwo.setEmail(utenteTwoId);
			Chat outputChat = DAOChat.getChatByUtente(inputUtenteOne, inputUtenteTwo);
			if (outputChat == null) {
				outputChat = DAOChat.getChatByUtente(inputUtenteTwo, inputUtenteOne);
			}
			return outputChat;
		}
		catch (WrappedCRUDException wcrude) {
			throw (wcrude.getWrappedException());
		}
	}
	
	@GetMapping("/all")
	public List<Chat> getAllChatWithUtente
	(
			@RequestParam String utenteId
	) throws Exception
	{
		if (utenteId.equals("")) throw new BadRequestWebException();
		try {
			DAOChat = DAOFactory.getDAOChat();
			return DAOChat.getAllChatWithUtente(utenteId);
		}
		catch (WrappedCRUDException wcrude) {
			throw (wcrude.getWrappedException());
		}
	}
	
	
	
	//Tutti i parametri non richiesti si escludono a vicenda.
	//Viene data priorità nel seguente ordine (discendente): 
	//numberOfMessaggioToGet: input: N messaggi da ottenere.
	//output: gli ultimi N messaggi.
	//currentNumberOfMessaggio: input: gli N messaggi della chat locale. 
	//output: restituisce i messaggi per i quali la chat locale è indietro.
	//getAllMessaggio: input: boolean che indica se si devono ottenere tutti i messaggi di una chat.
	//output: se l'input era true, restituisce tutti i messaggi della chat.
	@GetMapping("/messaggio")
	public List<Messaggio> getMessaggio
	(
			@RequestParam String utenteOneId,
			@RequestParam String utenteTwoId,
			@RequestParam(required = false) Long numberOfMessaggioToGet,
			@RequestParam(required = false) Long currentNumberOfMessaggio,
			@RequestParam(required = false) Boolean getAllMessaggio
	) throws Exception
	{
		ChatGetQuery chatGetQueryType;
		if (utenteOneId == null || utenteOneId.equals("") || utenteTwoId == null || utenteTwoId.equals(""))
			throw new BadRequestWebException();
		chatGetQueryType = decideGetQueryType(numberOfMessaggioToGet, currentNumberOfMessaggio, getAllMessaggio);
		try {
			DAOChat = DAOFactory.getDAOChat();
			Chat chatInput = new Chat();
			chatInput.setUtenteOneId(utenteOneId);
			chatInput.setUtenteTwoId(utenteTwoId);
			LinkedList<Messaggio> messaggioResult = new LinkedList<Messaggio>();
			
			if (chatGetQueryType == ChatGetQuery.GET_LAST_N_MESSAGGIO) {
				messaggioResult.addAll(DAOChat.getLastMessaggio(chatInput, numberOfMessaggioToGet));
			}
			else if (chatGetQueryType == ChatGetQuery.NUMBER_OF_MESSAGGIO_BEHIND) {
				long numberOfMessaggioBehind = -1 * DAOChat.checkIfChatIsUpToDate
						(utenteOneId, utenteTwoId, currentNumberOfMessaggio);
				if (numberOfMessaggioBehind > 0) {
					messaggioResult.addAll(DAOChat.getLastMessaggio(chatInput, numberOfMessaggioBehind));
				}
				
				//Caso interessante: in locale ci sono più messaggi rispetto a quanti ce ne siano nel DB.
				//Questo caso, con le dovute accorgenze del client, non dovrebbe mai verificarsi.
				else if (numberOfMessaggioBehind < 0) {
					throw new ServerException("Client has more messages than server. Please report this to the developers.");
				}
			}
			else if (chatGetQueryType == ChatGetQuery.GET_ALL_MESSAGGIO) {
				messaggioResult.addAll(DAOChat.getAllMessaggio(utenteOneId, utenteTwoId));
			}
			else throw new BadRequestWebException("No proper arguments provided.");
			return messaggioResult;
		}
		catch (WrappedCRUDException wcrude) {
			throw (wcrude.getWrappedException());
		}
	}
	
	//Output: id del nuovo messaggio.
	@MessageMapping("/messaggio/live/{utenteOneId}/{utenteTwoId}")
	public long insertMessaggio
	(
			Messaggio messaggio, 
			@DestinationVariable String utenteOneId,
			@DestinationVariable String utenteTwoId
	) throws Exception
	{
		if (utenteOneId == null || utenteOneId.equals("") || utenteTwoId == null || utenteTwoId.equals(""))
			throw new BadRequestWebException();
		DAOChat = DAOFactory.getDAOChat();
		Chat inputChat = new Chat();
		inputChat.setUtenteOneId(utenteOneId);
		inputChat.setUtenteTwoId(utenteTwoId);
		
		String sender, reciever;
		if (messaggio.isUtenteOneSender()) {
			sender = utenteOneId;
			reciever = utenteTwoId;
		}
		else {
			sender = utenteTwoId;
			reciever = utenteOneId;
		}
		try {
			//Controllo esistenza chat
			Utente utenteOne = new Utente();
			utenteOne.setEmail(inputChat.getUtenteOneId());
			Utente utenteTwo = new Utente();
			utenteTwo.setEmail(inputChat.getUtenteTwoId());
			Chat chat = DAOChat.getChatByUtente(utenteOne, utenteTwo);
			if (chat == null) {
				DAOChat.insertChat(inputChat);
			}
			//Inserimento messaggio
			long id = DAOChat.insertMessaggio(inputChat, messaggio);
			DTOMessaggio DTOMessaggio = new DTOMessaggio(sender, messaggio);
			this.messagingTemplate.convertAndSend("/chat/messaggio/live/" + sender, new DTOMessaggioInsertResponse(id));
			this.messagingTemplate.convertAndSend("/chat/messaggio/live/" + reciever, DTOMessaggio);
			return id;
		}
		catch (WrappedCRUDException | MessagingException e) {
			if (e instanceof MessagingException) throw e;
			else throw (((WrappedCRUDException)e).getWrappedException());
		}
	}
	
	private ChatGetQuery decideGetQueryType(Long numberOfMessaggioToGet, Long currentNumberOfMessaggio,
			Boolean getAllMessaggio) {
		if (numberOfMessaggioToGet != null && numberOfMessaggioToGet > 0) return ChatGetQuery.GET_LAST_N_MESSAGGIO;
		else if (currentNumberOfMessaggio != null && currentNumberOfMessaggio >= 0) return ChatGetQuery.NUMBER_OF_MESSAGGIO_BEHIND;
		else if (getAllMessaggio != null && getAllMessaggio) return ChatGetQuery.GET_ALL_MESSAGGIO;
		else return null;
	}
}
