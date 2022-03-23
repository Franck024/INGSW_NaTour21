package com.example.natour21;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class Controller_Home extends AppCompatActivity {

    private ImageView add_itin;
    private Animation anim_btn = null, anim_txtview = null;
    private TextView  user;

    ArrayList<ParentItem> parentItemArrayList;
    RecyclerView RVparent;


    //Prenderli dal DB
    String[] nomi = {"xxxxxxx", "yyyyyy", "zzzz", "pppp"};
    String[] diff = {"+", "-", "=", "+"};
    String[] tempo = {"12", "22", "3", "66"};
    String[] area = {"AAAA", "BBB", "CCC", "DDDD"};
    String[] utente = {"Tizio", "Maria", "Carlo", "Bob"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        add_itin = findViewById(R.id.btn_add_itin2);

        anim_btn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_bottone);
        anim_txtview = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_textview);

        add_itin.setOnClickListener(v -> {
            add_itin.startAnimation(anim_btn);
            startActivity(new Intent(Controller_Home.this, ControllerAddItin.class));
        });

// Creazione post
        RVparent = findViewById(R.id.RVparent2);
        parentItemArrayList = new ArrayList<>();



        for(int i = 0; i< nomi.length; i++){
            ParentItem parentItem = new ParentItem(nomi[i], diff[i], tempo[i], area[i], utente[i]);
            parentItemArrayList.add(parentItem);
        }
/// Click post
        MyAdapter2 myAdapter2 = new MyAdapter2(this,parentItemArrayList);
        RVparent.setAdapter(myAdapter2);
        RVparent.setLayoutManager(new LinearLayoutManager(this));
        ///END

        user = findViewById(R.id.textTitolo3);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Controller_Home.this, Controller_Utente.class));
            }
        });

    }

}

