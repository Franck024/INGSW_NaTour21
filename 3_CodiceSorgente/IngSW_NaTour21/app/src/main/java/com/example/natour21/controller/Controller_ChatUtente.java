package com.example.natour21.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.natour21.AdapterMessage;
import com.example.natour21.R;
import com.example.natour21.entities.Messaggio;
import java.time.OffsetDateTime;
import java.util.ArrayList;

public class Controller_ChatUtente extends AppCompatActivity {
    TextView nome_chat;
    ImageView back, invia;
    EditText message;
    RecyclerView RVmessaggi;

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

        nome_chat = findViewById(R.id.nomeChat);
        back = findViewById(R.id.backChat);
        message = findViewById(R.id.messageSend);
        invia = findViewById(R.id.inviaChat);
        RVmessaggi = findViewById(R.id.chatRecycleView);

        back.setOnClickListener(
                view -> startActivity(new Intent(Controller_ChatUtente.this, Controller_listChat.class)));

        //inserimento messaggi

        // Creazione lista messaggi
        nome_chat.setText("Nome persona");
        listMessage = new ArrayList<>();


        for (int i = 0; i < messaggi.length; i++) {
            Messaggio messageItem = new Messaggio(id[i], messaggi[i], null, isUtente[i]);
            listMessage.add(messageItem);
        }

        AdapterMessage listMessageAdapter = new AdapterMessage(this, listMessage, 2); // 1-> Mess ricevuti; 2 -> Mess inviati
        RVmessaggi.setAdapter(listMessageAdapter);


    }

}
