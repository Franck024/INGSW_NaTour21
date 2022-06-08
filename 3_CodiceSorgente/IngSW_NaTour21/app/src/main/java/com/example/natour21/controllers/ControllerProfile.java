package com.example.natour21.controllers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOItinerario;
import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.post.AdapterPost;
import com.example.natour21.post.Post;
import com.example.natour21.R;
import com.example.natour21.chat.stompclient.UserStompClient;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.entities.Utente;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.sharedprefs.UserSessionManager;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ControllerProfile extends AppCompatActivity implements java.util.Observer {
    private Button btnPlaylist, btnFoto;
    private ImageButton imageBtnStartChat, imageBtnAddItin, imageBtnSettings, imageBtnRicerca,
            imageBtnInbox, imageBtnHome;
    private RecyclerView recyclerViewPost;
    private ArrayList<Post> postArrayList;
    private Animation btn_menu = null;
    private DAOItinerario DAOItinerario;
    private DAOUtente DAOUtente;
    private String PROFILE_UTENTE_ID;
    private String PROFILE_UTENTE_DISPLAY_NAME;
    private TextView textViewUnreadMessaggiCount, textViewNomeUtente;

    @SuppressLint({"SetTextI18n", "UnsafeOptInUsageError"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        recyclerViewPost = findViewById(R.id.PostUtente);
        try{
            DAOItinerario = DAOFactory.getDAOItinerario();
            DAOUtente = DAOFactory.getDAOUtente();
        }
        catch (InvalidConnectionSettingsException icse){
            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                    (getApplicationContext(),"Profile", "Impossibile visualizzare il profilo.", icse);
            finish();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) PROFILE_UTENTE_ID = bundle.getString("USER_ID");
        if (PROFILE_UTENTE_ID == null){
            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                    (getApplicationContext(),"Profile", "Impossibile visualizzare il profilo.", null);
            finish();
        }

        String CURRENT_USER_ID = UserSessionManager.getInstance().getUserId();


        textViewNomeUtente = findViewById(R.id.textViewNomeUtente);
        btnPlaylist = findViewById(R.id.btnPlaylist);
        btnFoto = findViewById(R.id.btnFoto);
        imageBtnAddItin = findViewById(R.id.imageBtnAddItinerario);
        imageBtnStartChat = findViewById(R.id.btn_startChat);
        imageBtnSettings = findViewById(R.id.imageBtnSettings);
        textViewUnreadMessaggiCount = findViewById(R.id.textViewUnreadMessaggioCount);


        /// Click post
        AdapterPost adapterPost = new AdapterPost(postArrayList);
        recyclerViewPost.setAdapter(adapterPost);
        recyclerViewPost.setLayoutManager(new LinearLayoutManager(this));

        imageBtnHome = findViewById(R.id.imageBtnHome);
        imageBtnInbox = findViewById(R.id.imageBtnInbox);
        imageBtnRicerca = findViewById(R.id.imageBtnSearch);
        btnPlaylist = findViewById(R.id.btnPlaylist);
        btnFoto = findViewById(R.id.btnFoto);

        if (PROFILE_UTENTE_ID.equals(CURRENT_USER_ID)){
            imageBtnStartChat.setVisibility(View.INVISIBLE);
        }
        else{
            imageBtnStartChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ControllerProfile.this, ControllerChat.class);
                    intent.putExtra("OTHER_USER_ID", PROFILE_UTENTE_ID);
                    intent.putExtra("CHAT_NAME", PROFILE_UTENTE_DISPLAY_NAME);
                    startActivity(intent);
                }
            });
        }

        Callable<Long> getUnreadMessageCountCallable = () ->
                UserStompClient.getInstance().getUnreadMessageCountBlocking();


        Observable.fromCallable(getUnreadMessageCountCallable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result < 1) {
                        textViewUnreadMessaggiCount.setVisibility(View.INVISIBLE);
                        return;
                    }
                    textViewUnreadMessaggiCount.setText
                            ((result < 100) ? Math.toIntExact(result) + "" : "99+");
                    textViewUnreadMessaggiCount.setVisibility(View.VISIBLE);
                });



        imageBtnHome.setOnClickListener(view -> {
            imageBtnHome.animate().rotationY(360).withEndAction(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            startActivity(new Intent(ControllerProfile.this, ControllerHome.class));
                        }
                    });
        });

        imageBtnInbox.setOnClickListener(v -> {
            imageBtnInbox.animate().rotationY(360).withEndAction(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            startActivity(new Intent(ControllerProfile.this, ControllerInbox.class));
                        }
                    });
        });

        imageBtnRicerca.setOnClickListener(view -> {
            imageBtnRicerca.animate().rotationY(360).withEndAction(() -> startActivity(new Intent
                    (ControllerProfile.this, ControllerSearchItinerario.class)));
        });


        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Toast.makeText(ControllerProfile.this,
                            "Le playlist saranno implementate in un futuro aggiornamento !",
                            Toast.LENGTH_LONG).show();
            }
        });

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ControllerProfile.this,
                        "La raccolta foto sarà implementata in un futuro aggiornamento !",
                        Toast.LENGTH_LONG).show();
            }
        });

        Animation anim_btn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_btn);

        imageBtnAddItin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageBtnAddItin.startAnimation(anim_btn);
                startActivity(new Intent(ControllerProfile.this, ControllerAddNewItinerario.class));
            }
        });

        imageBtnSettings.setOnClickListener(view -> {
            startActivity(new Intent(ControllerProfile.this, ControllerSettings.class));
        });

        getAndSetItinerariUtente();

    }

    private void getAndSetItinerariUtente(){
        Callable<ArrayList<Post>> getItinerariUtenteCallable = () -> {
            if (PROFILE_UTENTE_DISPLAY_NAME == null){
                Utente utente = DAOUtente.getUtenteByEmail(PROFILE_UTENTE_ID);
                PROFILE_UTENTE_DISPLAY_NAME = utente.getDisplayName();
            }
            ArrayList<Post> postList = new ArrayList<>();
            for (Itinerario itinerario : DAOItinerario.getItinerarioByUtenteId(PROFILE_UTENTE_ID)){
                postList.add(new Post(itinerario, PROFILE_UTENTE_DISPLAY_NAME));
            }
            return postList;
        };
        FirebaseAnalytics firebaseAnalyticsInstance = FirebaseAnalytics.getInstance(ControllerProfile.this);
        Observable.fromCallable(getItinerariUtenteCallable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( parentItems -> {
                    AdapterPost adapterPost = new AdapterPost(parentItems);
                    AdapterPost.OnPostClickListener onPostClickListener = p -> {
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Long.toString(p.getIdItinerario()));
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "itinerario_profile");
                        firebaseAnalyticsInstance.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    };
                    recyclerViewPost.setAdapter(adapterPost);
                    recyclerViewPost.setLayoutManager(new LinearLayoutManager(this));
                    textViewNomeUtente.setText(PROFILE_UTENTE_DISPLAY_NAME);
                }, error -> ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                        (getApplicationContext(),"Profile", "Errore nel caricamento degli itinerari.", error)
                );
    }




    @SuppressLint("SetTextI18n")
    @Override
    public void update(java.util.Observable observable, Object o) {
        if (!(o instanceof Long)) return;
        if (textViewUnreadMessaggiCount == null) return;
        Long count = (Long) o;
        runOnUiThread(() -> {
            if (count < 1) {
                textViewUnreadMessaggiCount.setVisibility(View.INVISIBLE);
                return;
            }
            textViewUnreadMessaggiCount.setText
                    ((count < 100) ? Math.toIntExact(count) + "" : "99+");
            textViewUnreadMessaggiCount.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onBackPressed(){
        Intent feedIntent = new Intent
                (ControllerProfile.this, ControllerHome.class);
        startActivity(feedIntent);
    }
}

