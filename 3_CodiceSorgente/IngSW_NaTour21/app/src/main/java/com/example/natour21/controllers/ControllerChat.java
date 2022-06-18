package com.example.natour21.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.chat.stompclient.UserStompClient;
import com.example.natour21.chat.views.AdapterMessaggio;
import com.example.natour21.R;
import com.example.natour21.chat.views.viewmodels.ViewModelMessaggio;
import com.example.natour21.chat.views.viewmodels.ViewModelFactoryMessaggio;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ControllerChat extends AppCompatActivity {
    private TextView textViewChatName;
    private ImageView imageViewBack, imageViewSend;
    private EditText editTextNewMessaggio;
    private RecyclerView recyclerViewMessaggio;
    private ProgressBar progressBar;
    private ViewModelMessaggio viewModelMessaggio;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        textViewChatName = findViewById(R.id.textViewChatName);
        imageViewBack = findViewById(R.id.imageViewBack);
        editTextNewMessaggio = findViewById(R.id.editTextNewMessaggio);
        imageViewSend = findViewById(R.id.imageViewSend);
        recyclerViewMessaggio = findViewById(R.id.recyclerViewMessaggio);
        progressBar = findViewById(R.id.progressBar);

        Bundle bundle = getIntent().getExtras();
        if (!(bundle.containsKey("OTHER_USER_ID") && bundle.containsKey("CHAT_NAME"))){
            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                    (getApplicationContext(),"Chat", "Errore nell'inizializzare la chat.", null);
            finish();
        }
        String otherUserId = bundle.getString("OTHER_USER_ID");
        textViewChatName.setText(bundle.getString("CHAT_NAME"));

        imageViewBack.setOnClickListener(
                view -> startActivity(new Intent(ControllerChat.this, ControllerInbox.class)));

        // Creazione lista messaggi
        ViewModelFactoryMessaggio viewModelFactoryMessaggio = new ViewModelFactoryMessaggio(getApplication());
        viewModelFactoryMessaggio.setOtherUserId(otherUserId);
        viewModelMessaggio = new ViewModelProvider(this, viewModelFactoryMessaggio)
                .get(ViewModelMessaggio.class);

        // VISUALIZZAZIONI
        progressBar.setVisibility(View.GONE);
        recyclerViewMessaggio.setVisibility(View.VISIBLE);

        AdapterMessaggio listMessageAdapter = new AdapterMessaggio(new AdapterMessaggio.MessaggioDiff());
        recyclerViewMessaggio.setAdapter(listMessageAdapter);

        viewModelMessaggio.getAllMessaggi().observe(this, messaggi ->{
            listMessageAdapter.submitList(messaggi);
        });

        // Invio messaggi
        imageViewSend.setOnClickListener(v -> onSendClick(otherUserId));

        viewModelMessaggio.setChatRead(otherUserId);

    }

    private void onSendClick(String otherUserId){
        if(!TextUtils.isEmpty(editTextNewMessaggio.getText())){
            try{
                UserStompClient.getInstance().send(otherUserId,
                        editTextNewMessaggio.getText().toString());
                editTextNewMessaggio.setText(null);
            }
            catch (JsonProcessingException jpe){
                ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                        (getApplicationContext(),"Chat", "Impossibile mandare il messaggio.", jpe);
            }
        }
    }


}
