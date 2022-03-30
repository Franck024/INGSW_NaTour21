package com.example.natour21.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.dynamicfeatures.Constants;
import androidx.recyclerview.widget.RecyclerView;
import com.example.natour21.AdapterMessage;
import com.example.natour21.ConstantsChat;
import com.example.natour21.R;
import com.example.natour21.User;
import com.example.natour21.entities.Messaggio;
import com.example.natour21.entities.Utente;

import java.time.OffsetDateTime;
import java.util.ArrayList;

public class Controller_ChatUtente extends AppCompatActivity {
    TextView nome_chat;
    ImageView back, invia;
    EditText message;
    RecyclerView RVmessaggi;
    Context ct; // test
    ProgressBar progressBar;

    ArrayList<Messaggio> listMessage;

    //Prenderli dal DB
    long[] id = {0000,1111};
    String[] messaggi = {"xxxxxxx", "yyyyyy"};
//    OffsetDateTime[] orari = {OffsetDateTime.parse("1983-07-12T06:30:15+07:00"),  // da' problemi
 //           OffsetDateTime.parse("1983-07-12T06:30:17+40:00")};
    boolean[] isUtente = {true, false};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ct = (Context) this; // test

        nome_chat = findViewById(R.id.nomeChat);
        back = findViewById(R.id.backChat);
        message = findViewById(R.id.messageSend);
        invia = findViewById(R.id.inviaChat);
        RVmessaggi = findViewById(R.id.chatRecycleView);
        progressBar = findViewById(R.id.progressBar);

        nome_chat.setText(" "); // Passarlo tramite qualche entità
        back.setOnClickListener(
                view -> startActivity(new Intent(Controller_ChatUtente.this, Controller_listChat.class)));

        // Creazione lista messaggi
        nome_chat.setText("Nome persona");
        listMessage = new ArrayList<>();


        for (int i = 0; i < messaggi.length; i++) {
            Messaggio messageItem = new Messaggio(id[i], messaggi[i], null, isUtente[i]);
            listMessage.add(messageItem);
        }

        // VISUALIZZAZIONI
        progressBar.setVisibility(View.GONE);
        RVmessaggi.setVisibility(View.VISIBLE);

        AdapterMessage listMessageAdapter = new AdapterMessage(this, listMessage, 1); // 1-> Mess ricevuti; 2 -> Mess inviati
        RVmessaggi.setAdapter(listMessageAdapter);

        // Invio messaggi
        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(message.getText())){ // Controllo che il messaggio non è vuoto
                    //id mettere quello dell utente
                    Messaggio messSend = new Messaggio(0000,
                            message.getText().toString(), null, true);
                    listMessage.add(messSend);
                    AdapterMessage listMessageAdapter = new AdapterMessage(ct, listMessage, 2);
                    RVmessaggi.setAdapter(listMessageAdapter);
                    //svuota campo
                    message.setText(null);
                }
            }
        });

    }

}
