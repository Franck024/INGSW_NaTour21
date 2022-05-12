package com.example.natour21.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOItinerario;
import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.PostAdapter;
import com.example.natour21.ParentItem;
import com.example.natour21.R;
import com.example.natour21.chat.stompclient.UserStompClient;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.entities.Utente;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.sharedprefs.UserSessionManager;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class Controller_Utente extends AppCompatActivity implements java.util.Observer {
    private Button playlist, foto;
    private ImageButton btnStartChat, btnAddItin, btnSettings;
    private TextView home, messaggi;
    private RecyclerView RVutente;

    private DAOItinerario DAOItinerario;
    private DAOUtente DAOUtente;
    private String PROFILE_UTENTE_ID;
    private String PROFILE_UTENTE_DISPLAY_NAME;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        RVutente = findViewById(R.id.PostUtente);
        try{
            DAOItinerario = DAOFactory.getDAOItinerario();
            DAOUtente = DAOFactory.getDAOUtente();
        }
        catch (InvalidConnectionSettingsException icse){
            showErrorMessage(icse.getMessage());
            finish();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) PROFILE_UTENTE_ID = bundle.getString("USER_ID");
        if (PROFILE_UTENTE_ID == null){
            showErrorMessage("Impossibile visualizzare il profilo.");
            finish();
        }

        String CURRENT_USER_ID = UserSessionManager.getInstance().getUserId();

        home = findViewById(R.id.textViewHome);
        playlist = findViewById(R.id.btnPlaylist);
        foto = findViewById(R.id.btnFoto);
        messaggi = findViewById(R.id.textViewMessaggi);
        btnAddItin = findViewById(R.id.btn_add_itin2);
        Observable.just(UserStompClient.getInstance().getUnreadMessageCountBlocking())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> messaggi.setText("Messaggi" +
                        (result > 1
                        ? "(" + result + ")"
                        : ""
                        )
                    )
                );

        btnStartChat = findViewById(R.id.btn_startChat);
        btnSettings = findViewById(R.id.btnSettings);

        if (CURRENT_USER_ID.equals(UserSessionManager.getInstance().getUserId())){
            btnStartChat.setVisibility(View.GONE);
        }
        else{
            ((LinearLayout) findViewById(R.id.mainToolbarLayout)).setVisibility(View.GONE);
            btnStartChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Controller_Utente.this, Controller_ChatUtente.class);
                    intent.putExtra("OTHER_USER_ID", PROFILE_UTENTE_ID);
                    intent.putExtra("CHAT_NAME", PROFILE_UTENTE_DISPLAY_NAME);
                    startActivity(intent);
                }
            });
        }
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
                startActivity(new Intent(Controller_Utente.this, Controller_listChat.class));
            }
        });

        Animation anim_btn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_bottone);

        btnAddItin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddItin.startAnimation(anim_btn);
                startActivity(new Intent(Controller_Utente.this, ControllerAddItin.class));
            }
        });

        btnSettings.setOnClickListener(view -> {
            startActivity(new Intent(Controller_Utente.this, SettingsActivity.class));
        });

        getAndSetItinerariUtente();

    }

    private void getAndSetItinerariUtente(){
        Callable<ArrayList<ParentItem>> getItinerariUtenteCallable = () -> {
            if (PROFILE_UTENTE_DISPLAY_NAME == null){
                Utente utente = DAOUtente.getUtenteByEmail(PROFILE_UTENTE_ID);
                PROFILE_UTENTE_DISPLAY_NAME = utente.getNome() + " " + utente.getCognome();
            }
            ArrayList<ParentItem> parentItemList = new ArrayList<>();
            for (Itinerario itinerario : DAOItinerario.getItinerarioByUtenteId(PROFILE_UTENTE_ID)){
                parentItemList.add(itinerarioToParentItem(itinerario, PROFILE_UTENTE_DISPLAY_NAME));
            }
            return parentItemList;
        };
        Observable.fromCallable(getItinerariUtenteCallable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( parentItems -> {
                    PostAdapter postAdapter = new PostAdapter(this, parentItems);
                    RVutente.setAdapter(postAdapter);
                    RVutente.setLayoutManager(new LinearLayoutManager(this));
                }, error -> showErrorMessage(error.getMessage())
                );
    }

    private void showErrorMessage(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private ParentItem itinerarioToParentItem(Itinerario itinerario, String author){
        return new ParentItem(itinerario.getId(),
                itinerario.getNome(),
                itinerario.getDifficoltaItinerario(),
                itinerario.getDurata(),
                itinerario.getNomePuntoIniziale(),
                author);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void update(java.util.Observable observable, Object o) {
        if (!(o instanceof Long)) return;
        if ((Long) o < 1 ) return;
        runOnUiThread(() -> messaggi.setText("Messaggi" +
                ( (Long) o > 1
                        ? "(" + o + ")"
                        : ""
                )));
    }

    @Override
    public void onBackPressed(){
        Intent feedIntent = new Intent
                (Controller_Utente.this, Controller_Home.class);
        startActivity(feedIntent);
    }
}

