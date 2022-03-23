package com.example.natour21;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class Controller_Utente extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        ArrayList<ParentItem> parentItemArrayList;
        RecyclerView RVutente;

        //Prenderli dal DB
        String[] nomi = {"xxxxxxx", "yyyyyy", "zzzz", "pppp"};
        String[] diff = {"+", "-", "=", "+"};
        String[] tempo = {"12", "22", "3", "66"};
        String[] area = {"AAAA", "BBB", "CCC", "DDDD"};
        String[] utente = {"Tizio", "Maria", "Carlo", "Bob"};

        RVutente = findViewById(R.id.PostUtente);

        parentItemArrayList = new ArrayList<>();

        for (int i = 0; i < nomi.length; i++) {
            ParentItem parentItem = new ParentItem(nomi[i], diff[i], tempo[i], area[i], utente[i]);
            parentItemArrayList.add(parentItem);
        }

        RVutente.setLayoutManager(new LinearLayoutManager(this));
        RVutente.setAdapter(new MyAdapter(this, parentItemArrayList));
    }
}
