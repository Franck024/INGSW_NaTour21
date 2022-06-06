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

import com.example.natour21.chat.views.ListChatAdapter;
import com.example.natour21.R;
import com.example.natour21.chat.views.viewmodels.ChatViewModel;

public class ControllerMessageInbox extends AppCompatActivity {
    TextView nomeUtente;
    RecyclerView RVlistChat;
    ChatViewModel chatViewModel;
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        nomeUtente = findViewById(R.id.nome_utente);
        RVlistChat = findViewById(R.id.RVchat);
        back = findViewById(R.id.backListaMess);

        nomeUtente.setText("Chat");

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        ListChatAdapter listChatAdapter = new ListChatAdapter(new ListChatAdapter.ChatDiff());
        RVlistChat.setAdapter(listChatAdapter);
        RVlistChat.setLayoutManager(new LinearLayoutManager(this));

        chatViewModel.getChats().observe(this, chats ->
            listChatAdapter.submitList(chats));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ControllerMessageInbox.this, ControllerHome.class));
            }
        });
    }
}
