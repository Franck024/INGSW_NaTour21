package com.example.natour21.controller;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.ListChatAdapter;
import com.example.natour21.R;
import com.example.natour21.UtentiChat;

import java.util.ArrayList;

public class Controller_listChat extends AppCompatActivity {
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

        nomeUtente.setText("Chat");
        listChat = new ArrayList<>();


        for (int i = 0; i < nomi.length; i++) {
            UtentiChat chatItem = new UtentiChat(nomi[i]);
            listChat.add(chatItem);
        }

        ListChatAdapter listChatAdapter = new ListChatAdapter(this, listChat);
        RVlistChat.setAdapter(listChatAdapter);
        RVlistChat.setLayoutManager(new LinearLayoutManager(this));  /// Click chat

    }
}
