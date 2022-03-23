package com.example.natour21;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class Controller_Utente extends AppCompatActivity {
    Button playlist, foto;
    ImageButton messaggi;
    TextView home;
    ArrayList<ParentItem> parentItemArrayList;
    RecyclerView RVutente;

    //Prenderli dal DB
    String[] nomi = {"xxxxxxx", "yyyyyy", "zzzz", "pppp"};
    String[] diff = {"+", "-", "=", "+"};
    String[] tempo = {"12", "22", "3", "66"};
    String[] area = {"AAAA", "BBB", "CCC", "DDDD"};
    String[] utente = {"Tizio", "Maria", "Carlo", "Bob"};


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        // Creazione post
        RVutente = findViewById(R.id.PostUtente);
        parentItemArrayList = new ArrayList<>();



        for(int i = 0; i< nomi.length; i++){
            ParentItem parentItem = new ParentItem(nomi[i], diff[i], tempo[i], area[i], utente[i]);
            parentItemArrayList.add(parentItem);
        }
        /// Click post
        MyAdapter2 myAdapter2 = new MyAdapter2(this,parentItemArrayList);
        RVutente.setAdapter(myAdapter2);
        RVutente.setLayoutManager(new LinearLayoutManager(this));
        ///END

        home = findViewById(R.id.Tw_home);
        playlist = findViewById(R.id.btnPlaylist);
        foto = findViewById(R.id.btnFoto);
        messaggi = findViewById(R.id.btn_messaggi);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Controller_Utente.this, Controller_Home.class));
            }
        });

        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Toast.makeText(Controller_Utente.this,
                            "Le playlist saranno implementate in un futuro aggiornamento !",
                            Toast.LENGTH_LONG).show();
            }
        });

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Controller_Utente.this,
                        "La raccolta foto sarà implementata in un futuro aggiornamento !",
                        Toast.LENGTH_LONG).show();
            }
        });

        messaggi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Controller_Utente.this, Controller_Messaggi.class));
            }
        });

    }
}
