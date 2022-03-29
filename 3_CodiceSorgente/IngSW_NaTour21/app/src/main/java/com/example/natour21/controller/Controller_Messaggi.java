package com.example.natour21.controller;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.ChatAdapter;
import com.example.natour21.ConstantsChat;
import com.example.natour21.ListChatAdapter;
import com.example.natour21.ParentItem;
import com.example.natour21.PostAdapter;
import com.example.natour21.R;
import com.example.natour21.UtentiChat;

import java.util.ArrayList;
import java.util.HashMap;


// classe MessagingService video2 min 6.45
public class Controller_Messaggi extends AppCompatActivity {
    /*


  //  private ActivityMainBinding binding;
//    private User receiverUser;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    // private Firebase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

    }

    //Invio messaggi
    private void init(){
   //     preferenceManager = new PreferenceManager(getApplicationContext());
    //    chatAdapter = new ChatAdapter(
    //            chatMessage, preferenceManager.getString(ConstantsChat.KEY_USER_ID)
   //     );
    //    bind
    }

    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
    //    message.put(ConstantsChat.KEY_SENDER_ID, prefere)
    }
    /////


    */


    TextView nomeUtente;
    ArrayList<UtentiChat> listChat;
    RecyclerView RVlistChat;

    //Prenderli dal DB
    String[] nomi = {"xxxxxxx", "yyyyyy", "zzzz", "pppp"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        // Creazione lista chat
        nomeUtente = findViewById(R.id.nome_utente);
        RVlistChat = findViewById(R.id.RVchat);

        nomeUtente.setText("MIO NOME");
        listChat = new ArrayList<>();



        for(int i = 0; i< nomi.length; i++){
            UtentiChat chatItem = new UtentiChat(nomi[i]);
            listChat.add(chatItem);
        }
        /// Click post
        ListChatAdapter listChatAdapter = new ListChatAdapter(this, listChat);
        RVlistChat.setAdapter(listChatAdapter);
        RVlistChat.setLayoutManager(new LinearLayoutManager(this));
        ///END
    }
}
