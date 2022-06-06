package com.example.natour21.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.chat.stompclient.UserStompClient;
import com.example.natour21.chat.views.AdapterMessage;
import com.example.natour21.R;
import com.example.natour21.chat.views.viewmodels.MessaggioViewModel;
import com.example.natour21.chat.views.viewmodels.MessaggioViewModelFactory;
import com.example.natour21.entities.Messaggio;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.OffsetDateTime;
import java.util.ArrayList;

public class Controller_ChatUtente extends AppCompatActivity {
    TextView nome_chat;
    ImageView back, invia;
    EditText editTextNewMessaggio;
    RecyclerView RVmessaggi;
    ProgressBar progressBar;

    MessaggioViewModel messaggioViewModel;

    ArrayList<Messaggio> listMessage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        nome_chat = findViewById(R.id.nomeChat);
        back = findViewById(R.id.backChat);
        editTextNewMessaggio = findViewById(R.id.messageSend);
        invia = findViewById(R.id.inviaChat);
        RVmessaggi = findViewById(R.id.chatRecycleView);
        progressBar = findViewById(R.id.progressBar);

        Bundle bundle = getIntent().getExtras();
        if (!(bundle.containsKey("OTHER_USER_ID") && bundle.containsKey("CHAT_NAME"))){
            showErrorMessage("Errore nell'inizializzare la chat.");
            finish();
        }
        String otherUserId = bundle.getString("OTHER_USER_ID");
        nome_chat.setText(bundle.getString("CHAT_NAME"));

        back.setOnClickListener(
                view -> startActivity(new Intent(Controller_ChatUtente.this, Controller_listChat.class)));

        // Creazione lista messaggi
        MessaggioViewModelFactory messaggioViewModelFactory = new MessaggioViewModelFactory(getApplication());
        messaggioViewModelFactory.setOtherUserId(otherUserId);
        messaggioViewModel = new ViewModelProvider(this, messaggioViewModelFactory)
                .get(MessaggioViewModel.class);

        // VISUALIZZAZIONI
        progressBar.setVisibility(View.GONE);
        RVmessaggi.setVisibility(View.VISIBLE);

        AdapterMessage listMessageAdapter = new AdapterMessage(new AdapterMessage.MessageDiff());
        RVmessaggi.setAdapter(listMessageAdapter);

        messaggioViewModel.getAllMessaggi().observe(this, messaggi ->{
            listMessageAdapter.submitList(messaggi);
        });

        // Invio messaggi
        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(editTextNewMessaggio.getText())){
                    try{
                        UserStompClient.getInstance().send(otherUserId,
                                editTextNewMessaggio.getText().toString());
                        editTextNewMessaggio.setText(null);
                    }
                    catch (JsonProcessingException jpe){
                        showErrorMessage(jpe.getMessage());
                    }
                }
            }
        });

        messaggioViewModel.setChatRead(otherUserId);

    }

    private void showErrorMessage(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}
