package com.example.natour21.chat.stompclient;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOChat;
import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.DTOs.DTOMessaggio;
import com.example.natour21.DTOs.DTOMessaggioInsertResponse;
import com.example.natour21.chat.MessaggioUtils;
import com.example.natour21.chat.room.entities.ChatDBEntity;
import com.example.natour21.chat.room.entities.MessaggioDBEntity;
import com.example.natour21.chat.room.repositories.ChatRepository;
import com.example.natour21.chat.room.repositories.MessaggioRepository;
import com.example.natour21.entities.Chat;
import com.example.natour21.entities.Messaggio;
import com.example.natour21.entities.Utente;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.exceptions.NoSuchUserInUtenteOneHashMap;
import com.example.natour21.exceptions.UninitializedUserStompClientException;
import com.example.natour21.sharedprefs.UserSessionManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class UserStompClient extends java.util.Observable {

    private static UserStompClient instance;
    private StompClient stompClient = null;
    private ChatRepository chatRepository;
    private MessaggioRepository messaggioRepository;
    private DAOUtente DAOUtente;
    private DAOChat DAOChat;
    private final String BASE_URL = "192.168.1.220";
    private final int PORT = 5000;
    private final String ENDPOINT = "websocket-endpoint";
    private final String WEBSOCKET_PREFIX = "/chat/messaggio/live/";
    private long unreadMessageCount = 0;
    //Usato per non permettere all'utente di collegarsi a meno che sia realmente autenticato.
    private boolean isEnabled = false;
    private HashMap<String, Boolean> isCurrentUtenteUtenteOneInBackendHashMap = new HashMap<>();

    private class MessaggioChatIdPair{
        private Messaggio messaggio;
        private String chatId;

        public MessaggioChatIdPair(Messaggio messaggio, String chatId) {
            this.messaggio = messaggio;
            this.chatId = chatId;
        }

        public Messaggio getMessaggio() {
            return messaggio;
        }

        public void setMessaggio(Messaggio messaggio) {
            this.messaggio = messaggio;
        }

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
        }
    }
    private LinkedList<MessaggioChatIdPair> selfMessaggioChatIdPairBuffer = new LinkedList<>();

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public static void init(Application application) throws InvalidConnectionSettingsException{
        instance = new UserStompClient(application);
    }
    public static UserStompClient getInstance(){
        if (instance == null) throw
                new UninitializedUserStompClientException();
        return instance;
    }

    private UserStompClient(Application application) throws InvalidConnectionSettingsException {
        chatRepository = new ChatRepository(application);
        messaggioRepository = new MessaggioRepository(application);
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,
                "ws://" + BASE_URL + ":" + PORT + "/" + ENDPOINT + "/websocket");
        DAOUtente = DAOFactory.getDAOUtente();
        DAOChat = DAOFactory.getDAOChat();
    }

    public void setEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }

    @SuppressLint("CheckResult")
    public void connect(){
        if (!(UserSessionManager.getInstance().isLoggedIn()) || !isEnabled) return;
        Log.i("STOMP-CONNECT", "Connecting at time " + Calendar.getInstance().getTime());
        Completable.fromCallable(getSyncChatsCallable())
                .subscribeOn(Schedulers.io())
                .andThen(Completable.defer(() -> Completable.fromCallable(getConnectCallable())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( () -> Log.i
                                ("STOMP-CONNECT", "Established connection at time " + Calendar.getInstance().getTime()),
                        error -> Log.e("STOMP-CONNECT", error.getMessage()));
    }

    public void disconnect(){
        stompClient.disconnect();
        Log.i("STOMP-DISCONNECT", "Stomp client disconnected by manual disconnect call.");
    }

    public boolean isConnected() { return stompClient.isConnected();}

    public long getUnreadMessageCountBlocking(){
        if (stompClient.isConnected()) return unreadMessageCount;
        unreadMessageCount = chatRepository.getUnreadMessageCountBlocking();
        return unreadMessageCount;
    }

    private Callable<Void> getSyncChatsCallable(){
        Callable<Void> syncChatsCallable = () -> {
            List<ChatDBEntity> chats = chatRepository.getAllChatsNonLiveDataBlocking();
            String currentUtenteId = getCurrentUtenteId();
            List<Chat> remoteChats = DAOChat.getAllChatWithUtente(currentUtenteId);
            //Ottenere tutte le chat remote, iterare attraverso loro per controllare che siano
            //inserite nel DB locale
            for (Chat remoteChat : remoteChats){
                boolean found = false;
                for (ChatDBEntity chat : chats){
                    String remoteOtherUserId;
                    if (chat.isCurrentUtenteUtenteOneInBackend()){
                        remoteOtherUserId = remoteChat.getUtenteTwoId();
                    } else remoteOtherUserId = remoteChat.getUtenteOneId();
                    if (chat.getOtherUserId().equals(remoteOtherUserId)){
                        found = true;
                        break;
                    }
                } if (found) continue;
                //Se la chat non è stata trovata, si procede all'inserimento
                String otherUserId;
                boolean isCurrentUtenteUtenteOneInBackend;
                if (currentUtenteId.equals(remoteChat.getUtenteOneId())){
                    isCurrentUtenteUtenteOneInBackend = true;
                    otherUserId = remoteChat.getUtenteTwoId();
                } else {
                    isCurrentUtenteUtenteOneInBackend = false;
                    otherUserId = remoteChat.getUtenteOneId();
                }
                Utente otherUtente = DAOUtente.getUtenteByEmail(otherUserId);
                String displayName = otherUtente.getDisplayName();
                ChatDBEntity newLocalChat = new
                        ChatDBEntity(otherUserId, displayName, isCurrentUtenteUtenteOneInBackend);
                chatRepository.insertBlocking(newLocalChat);
                chats.add(newLocalChat);
            }
            //Fine aggiornamento delle chat
            //Si procede con l'aggiornamento dei messaggi, e con essi,
            //l'aggiornamento del numero dei messaggi non letti.
            long newMessaggioCount = 0;
            long localUnreadMessageCount = 0;
            for (ChatDBEntity localDBChat : chats){
                isCurrentUtenteUtenteOneInBackendHashMap.put(localDBChat.getOtherUserId(),
                        localDBChat.isCurrentUtenteUtenteOneInBackend());
                localUnreadMessageCount += localDBChat.getUnreadMessageCount();
                String utenteOne, utenteTwo;
                if (localDBChat.isCurrentUtenteUtenteOneInBackend()){
                    utenteOne = currentUtenteId;
                    utenteTwo = localDBChat.getOtherUserId();
                }
                else{
                    utenteOne = localDBChat.getOtherUserId();
                    utenteTwo = currentUtenteId;
                }
                Chat chat = new Chat(utenteOne, utenteTwo);
                boolean isMainUtenteSender;
                List<Messaggio> newMessaggi = DAOChat.getMissingMessaggio
                        (chat, localDBChat.getMessageCount());
                for (Messaggio messaggio : newMessaggi){
                    newMessaggioCount++;
                    isMainUtenteSender = messaggio.isUtenteOneSender() ==
                            localDBChat.isCurrentUtenteUtenteOneInBackend();
                    MessaggioDBEntity newLocalMessaggio =
                            new MessaggioDBEntity(messaggio.getId(),
                                    localDBChat.getOtherUserId(),
                                    messaggio.getTesto(),
                                    messaggio.getTimestamp(),
                                    isMainUtenteSender);
                    messaggioRepository.insertBlocking(newLocalMessaggio);
                }

            }
            if (newMessaggioCount == 0) return null;
            localUnreadMessageCount += newMessaggioCount;
            unreadMessageCount = localUnreadMessageCount;
            setChanged();
            notifyObservers(unreadMessageCount);
            return null;
        };
        return syncChatsCallable;
    }

    private Callable<Void> getConnectCallable(){
        @SuppressLint("CheckResult")
        Callable<Void> connectCallable = () -> {
            stompClient.connect();
            stompClient.topic(WEBSOCKET_PREFIX + getCurrentUtenteId())
                    .subscribe(topicMessage -> {
                        Object obj;
                        final MessaggioDBEntity messaggioDBEntity;
                        try{
                            obj = objectMapper.readValue(topicMessage.getPayload(), DTOMessaggio.class);
                            String sender = ((DTOMessaggio)obj).getSender();
                            messaggioDBEntity = MessaggioUtils.convertToMessaggioDBEntity(((DTOMessaggio)obj));
                            Callable<Void> onMessageReceivedCallable = () -> {
                                setChanged();
                                boolean doesChatWithUserExistLocally = chatRepository
                                        .doesChatWithUserExistBlocking(sender);
                                if (!doesChatWithUserExistLocally) {
                                    Utente utente = DAOUtente.getUtenteByEmail(sender);
                                    String id = utente.getEmail();
                                    String nomeChat = utente.getDisplayName();
                                    Utente inputCurrentUtente = new Utente
                                            (getCurrentUtenteId(), "", "", false);
                                    Chat chat = DAOChat.getChatByUtente(utente, inputCurrentUtente);
                                    //Se la chat non è stata ancora inserita nel backend, annulla l'operazione
                                    if (chat == null) return null;
                                    boolean isCurrentUtenteUtenteOneInBackend =
                                            chat.getUtenteOneId().equals(inputCurrentUtente.getEmail());
                                    chatRepository.insertBlocking(new ChatDBEntity
                                            (id, nomeChat, isCurrentUtenteUtenteOneInBackend));
                                }
                                messaggioRepository.insertBlocking(messaggioDBEntity);

                                return null;
                            };
                            Completable.fromCallable(onMessageReceivedCallable)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(()-> notifyObservers(++unreadMessageCount),
                                            error -> Log.e("UserStompClient", error.getMessage(), error));
                        }
                        //Supponiamo che si sia verificata un eccezione per errore di conversione
                        catch (Exception firstException){
                            try{
                                obj = objectMapper.readValue(topicMessage.getPayload(),
                                        DTOMessaggioInsertResponse.class);
                                MessaggioChatIdPair messaggioChatIdPair = selfMessaggioChatIdPairBuffer.peekFirst();
                                MessaggioDBEntity messaggioDBEntityToInsert  = MessaggioUtils.convertToMessaggioDBEntity
                                                ((DTOMessaggioInsertResponse) obj,
                                                messaggioChatIdPair.getMessaggio(),
                                                messaggioChatIdPair.getChatId());
                                selfMessaggioChatIdPairBuffer.removeFirst();
                                Callable<Void> insertMessaggioCallable = () ->
                                {
                                    boolean doesChatWithUserExistLocally = chatRepository
                                            .doesChatWithUserExistBlocking(messaggioChatIdPair.getChatId());
                                    if (!doesChatWithUserExistLocally) {
                                        Utente utente = DAOUtente.getUtenteByEmail(messaggioChatIdPair.getChatId());
                                        String id = utente.getEmail();
                                        String nomeChat = utente.getDisplayName();
                                        Utente inputCurrentUtente = new Utente
                                                (getCurrentUtenteId(), "", "", false);
                                        Chat chat = DAOChat.getChatByUtente(utente, inputCurrentUtente);
                                        //Se la chat non è stata ancora inserita nel backend, annulla l'operazione
                                        if (chat == null) return null;
                                        boolean isCurrentUtenteUtenteOneInBackend =
                                                chat.getUtenteOneId().equals(inputCurrentUtente.getEmail());
                                        chatRepository.insertBlocking(new ChatDBEntity
                                                (id, nomeChat, isCurrentUtenteUtenteOneInBackend));
                                    }
                                    messaggioRepository.insert(messaggioDBEntityToInsert);
                                    return null;
                                };

                                Completable.fromCallable(insertMessaggioCallable)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe( () -> {},
                                                error ->  Log.e("UserStompClient", error.getMessage(), error));
                            }
                            catch (Exception secondException)
                            {
                                obj = secondException;
                                setChanged();
                                notifyObservers(obj);
                            }
                        }

                    });
            return null;
        };
        return connectCallable;
    }


    public void send(String receiver, String text) throws JsonProcessingException, NoSuchUserInUtenteOneHashMap {
        Boolean isCurrentUtenteUtenteOneInBackend = isCurrentUtenteUtenteOneInBackendHashMap.get(receiver);
        if (isCurrentUtenteUtenteOneInBackend == null){
            isCurrentUtenteUtenteOneInBackend = true;
            isCurrentUtenteUtenteOneInBackendHashMap.put(receiver, isCurrentUtenteUtenteOneInBackend);
        }
        Messaggio messaggio = new Messaggio(0,
                text,
                OffsetDateTime.now(),
                isCurrentUtenteUtenteOneInBackend);
        selfMessaggioChatIdPairBuffer.add(new MessaggioChatIdPair(messaggio, receiver));
        String subpath = isCurrentUtenteUtenteOneInBackend
                ? getCurrentUtenteId() + "/" + receiver
                : receiver + "/" + getCurrentUtenteId();
        stompClient.send(WEBSOCKET_PREFIX + subpath,
                objectMapper.writeValueAsString(messaggio)).subscribe();

    }

    private String getCurrentUtenteId(){
        return UserSessionManager.getInstance().getUserId();
    }

}
