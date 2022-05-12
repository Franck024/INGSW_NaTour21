package com.example.natour21.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.example.natour21.exceptions.WrappedCRUDException;
import com.example.natour21.sharedprefs.UserSessionManager;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.http.POST;

public class Controller_Home extends AppCompatActivity implements java.util.Observer {

    private ImageButton add_itin, btnSettings;
    private Animation anim_btn = null, anim_txtview = null;
    private TextView profilo, messaggi;
    private final int POSTS_PER_REFRESH = 10;
    private DAOItinerario DAOItinerario;
    private DAOUtente DAOUtente;
    private boolean isNotPullRefresh = false;
    private boolean isUpdating = false;
    SwipeRefreshLayout refreshLayout;

    PostAdapter feedPostAdapter;
    RecyclerView RVparent;




    //"LATEST" indica gli ultimi post quando il feed è vuoto.
    //"NEWER" indica i post più nuovi dell'ultimo post caricato, quando l'utente richiede un pull refresh.
    //"OLDER" è per quando l'utente raggiunge la fine del feed e bisogna caricare altri vecchi post.
    private enum UpdateType{
        LATEST,
        NEWER,
        OLDER
    }

    private long oldestLoadedPostId = 0;
    private long newestLoadedPostId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        UserStompClient.getInstance().addObserver(this);

        add_itin = findViewById(R.id.btn_add_itin2);
        btnSettings = findViewById(R.id.btnSettings);

        anim_btn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_bottone);
        anim_txtview = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_textview);

        add_itin.setOnClickListener(v -> {
            add_itin.startAnimation(anim_btn);
            startActivity(new Intent(Controller_Home.this, ControllerAddItin.class));
        });

        btnSettings.setOnClickListener(view -> {
            startActivity(new Intent(Controller_Home.this, SettingsActivity.class));
        });

        refreshLayout = findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener(){
                    @Override
                    public void onRefresh(){
                        if (isNotPullRefresh) return;
                        updateFeed(UpdateType.NEWER);
                    }

                });
                // Creazione post
                RVparent = findViewById(R.id.RVparent2);

        try{
            DAOItinerario = DAOFactory.getDAOItinerario();
            DAOUtente = DAOFactory.getDAOUtente();
        }
        catch (InvalidConnectionSettingsException icse){

        }
        // Click post
        feedPostAdapter = new PostAdapter(this, new ArrayList<ParentItem>());
        RVparent.setAdapter(feedPostAdapter);
        isNotPullRefresh = true;
        refreshLayout.setRefreshing(true);
        updateFeed(UpdateType.LATEST);
        RVparent.setLayoutManager(new LinearLayoutManager(this));
        RVparent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    isNotPullRefresh = true;
                    refreshLayout.setRefreshing(true);
                    updateFeed(UpdateType.OLDER);
                }
            }
        });
        ///END

        profilo = findViewById(R.id.textViewUtente);
        profilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(Controller_Home.this, Controller_Utente.class);
                profileIntent.putExtra("USER_ID", UserSessionManager.getInstance().getUserId());
                startActivity(profileIntent);
            }
        });

        messaggi = findViewById(R.id.textViewMessaggi);

        Callable<Long> getUnreadMessageCountCallable = () ->
            UserStompClient.getInstance().getUnreadMessageCountBlocking();


        Observable.fromCallable(getUnreadMessageCountCallable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> messaggi.setText(messaggi.getText() + "(" + result +")"));

        messaggi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Controller_Home.this, Controller_listChat.class));
            }
        });

    }

    private void onUpdateError(Throwable throwable, UpdateType updateType){
        String errorMessage = (throwable instanceof Exception)
                ? ((Exception) throwable).getMessage()
                : "Impossibile aggiornare il feed.";
        System.out.println(errorMessage);
        Snackbar.make(
                RVparent,
                errorMessage,
                BaseTransientBottomBar.LENGTH_LONG
        ).setDuration(8000)
                .setAction("Riprova", new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        isNotPullRefresh = true;
                        refreshLayout.setRefreshing(true);
                        updateFeed(updateType);
                    }
                }).show();
    }

    private void onUpdateFeedUnchanged(UpdateType updateType){
        String errorMessage = "Fine feed raggiunta.";
        if (updateType.equals(UpdateType.LATEST) || updateType.equals(UpdateType.NEWER))
            errorMessage =  "Non ci sono nuovi post da mostrare. Torna più tardi";
        else if (updateType.equals(UpdateType.OLDER))
            errorMessage = "Hai visto tutti i post dell'app. Torna più tardi.";
        Toast.makeText(getApplicationContext(),
                errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void updateFeed(UpdateType updateType){
        if (isUpdating) return;
        Callable<Boolean> callable = null;
        ArrayList<ParentItem> parentItemArrayList = new ArrayList<>();
        if (updateType.equals(UpdateType.LATEST)){
            callable = new Callable<Boolean>(){
                @Override
                public Boolean call() throws Exception{
                    return getLatestPosts(parentItemArrayList);
                }
            };
        }
        else if (updateType.equals(UpdateType.OLDER)){
            callable = new Callable<Boolean>(){
                @Override
                public Boolean call() throws Exception{
                    return getOlderPosts(parentItemArrayList);
                }
            };
        }
        else if (updateType.equals(UpdateType.NEWER)){
            callable = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return getNewerPosts(parentItemArrayList);
                }
            };
        }
        else return;
        isUpdating = true;
        Observable.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hasFeedChanged -> {
                    if (hasFeedChanged){
                        if (updateType.equals(UpdateType.OLDER))
                            feedPostAdapter.addAll(parentItemArrayList);
                        else if (updateType.equals(UpdateType.LATEST) || updateType.equals(UpdateType.NEWER))
                            feedPostAdapter.addAllAtIndex(0, parentItemArrayList);

                    } else onUpdateFeedUnchanged(updateType);
                    isUpdating = false;
                    isNotPullRefresh = false;
                    refreshLayout.setRefreshing(false);
                }, error -> {
                            onUpdateError(error, updateType);
                            isUpdating = false;
                            isNotPullRefresh = false;
                            refreshLayout.setRefreshing(false);
                        });
    }

    @Override
    public void update(java.util.Observable observable, Object o) {
        if (!(o instanceof Long)) return;
        if ((Long) o < 1 ) return;
        runOnUiThread(() -> messaggi.setText("Messaggi " + " (" + o + ")"));
    }

    private boolean getNewerPosts(List<ParentItem> list) throws WrappedCRUDException{
        List<Itinerario> itinerarioList = DAOItinerario.getLastNItinerarioNewerThan
                (newestLoadedPostId, POSTS_PER_REFRESH);
        List<ParentItem> parentItemList = new LinkedList<ParentItem>();
        Utente utente;
        int itinerarioCounter = 0;
        for (Itinerario i : itinerarioList){
            if (itinerarioCounter == 0) newestLoadedPostId = i.getId();
            utente = DAOUtente.getUtenteByEmail(i.getAuthorId());
            parentItemList.add(itinerarioToParentItem
                    (i, utente.getDisplayName()));
            itinerarioCounter++;
        }
        return list.addAll(parentItemList);
    }

    private boolean getOlderPosts(List<ParentItem> list) throws WrappedCRUDException{
        List<Itinerario> itinerarioList = DAOItinerario.getLastNItinerarioStartingFrom
                (oldestLoadedPostId-1, POSTS_PER_REFRESH);
        List<ParentItem> parentItemList = new LinkedList<ParentItem>();
        Utente utente;
        int itinerarioCounter = 0;
        for (Itinerario i : itinerarioList){
            utente = DAOUtente.getUtenteByEmail(i.getAuthorId());
            parentItemList.add(itinerarioToParentItem
                    (i, utente.getDisplayName()));
            itinerarioCounter++;
            if (itinerarioCounter == itinerarioList.size())
                oldestLoadedPostId = i.getId();
        }
        return list.addAll(parentItemList);
    }

    private boolean getLatestPosts(List<ParentItem> list) throws WrappedCRUDException {

        List<Itinerario> latestItinerario = DAOItinerario.getLastNItinerario(POSTS_PER_REFRESH);
        List<ParentItem> parentItemList = new LinkedList<ParentItem>();
        Utente utente;
        int itinerarioCounter = 0;
        for (Itinerario i : latestItinerario){
            if (itinerarioCounter == 0) newestLoadedPostId = i.getId();
            utente = DAOUtente.getUtenteByEmail(i.getAuthorId());
            parentItemList.add(itinerarioToParentItem
                    (i, utente.getNome() + utente.getCognome()));
            itinerarioCounter++;
            if (itinerarioCounter == latestItinerario.size())
                oldestLoadedPostId = i.getId();
        }
        return list.addAll(0, parentItemList);
    }

    private ParentItem itinerarioToParentItem(Itinerario itinerario, String author){
        return new ParentItem(itinerario.getId(),
                itinerario.getNome(),
                itinerario.getDifficoltaItinerario(),
                itinerario.getDurata(),
                itinerario.getNomePuntoIniziale(),
                author);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        UserStompClient.getInstance().deleteObserver(this);
    }

}

