package com.example.natour21;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Controller_Messaggi extends AppCompatActivity {

    ArrayList<UtentiChat> ListChat;
    RecyclerView RVchat;


    //Prenderli dal DB
    String[] nomi = {"xxxxxxx", "yyyyyy", "zzzz", "pppp"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);
        // Carico post
        RVchat = findViewById(R.id.RVchat);

        ListChat = new ArrayList<>();

        for (int i = 0; i < nomi.length; i++) {
            UtentiChat chatItem = new UtentiChat(nomi[i]);
            ListChat.add(chatItem);
        }

        RVchat.setLayoutManager(new LinearLayoutManager(this));
        RVchat.setAdapter(new AdapterChat(this, ListChat));

        ///
    }
}
