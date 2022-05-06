package com.example.natour21.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
    ImageView back;

    //Prenderli dal DB
    String[] nomi = {"xxxxxxx", "yyyyyy", "zzzz", "pppp"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        // Creazione lista chat
        nomeUtente = findViewById(R.id.nome_utente);
        RVlistChat = findViewById(R.id.RVchat);
        back = findViewById(R.id.backListaMess);

        nomeUtente.setText("Chat");
        listChat = new ArrayList<>();


        for (int i = 0; i < nomi.length; i++) {
            UtentiChat chatItem = new UtentiChat(nomi[i]);
            listChat.add(chatItem);
        }

        ListChatAdapter listChatAdapter = new ListChatAdapter(this, listChat);
        RVlistChat.setAdapter(listChatAdapter);
        RVlistChat.setLayoutManager(new LinearLayoutManager(this));  /// Click chat
//FINE CREAZIONE

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back.animate().rotationY(360).withEndAction(
                        new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(Controller_listChat.this, Controller_Home.class));
                            }
                        });
            }
        });
    }
}
