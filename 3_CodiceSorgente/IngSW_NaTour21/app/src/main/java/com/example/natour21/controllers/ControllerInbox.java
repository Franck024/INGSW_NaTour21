package com.example.natour21.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.chat.views.AdapterChat;
import com.example.natour21.R;
import com.example.natour21.chat.views.viewmodels.ViewModelChat;

public class ControllerInbox extends AppCompatActivity {
    private TextView textViewNomeUtente;
    private RecyclerView recyclerViewChat;
    private ViewModelChat viewModelChat;
    private ImageView imageViewBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        textViewNomeUtente = findViewById(R.id.nome_utente);
        recyclerViewChat = findViewById(R.id.RVchat);
        imageViewBack = findViewById(R.id.backListaMess);

        textViewNomeUtente.setText("Chat");

        viewModelChat = new ViewModelProvider(this).get(ViewModelChat.class);
        AdapterChat adapterChat = new AdapterChat(new AdapterChat.DiffChat());
        recyclerViewChat.setAdapter(adapterChat);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));

        viewModelChat.getChats().observe(this, chats ->
            adapterChat.submitList(chats));
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ControllerInbox.this, ControllerHome.class));
            }
        });
    }
}
