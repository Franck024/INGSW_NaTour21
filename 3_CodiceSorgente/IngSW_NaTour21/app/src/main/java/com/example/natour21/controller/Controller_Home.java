package com.example.natour21.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOItinerario;
import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.MainActivity;
import com.example.natour21.PostAdapter;
import com.example.natour21.ParentItem;
import com.example.natour21.R;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.entities.Utente;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.exceptions.WrappedCRUDException;
import com.example.natour21.multithreading.ThreadManager;
import com.example.natour21.network.NetworkCallable;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DefaultObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Controller_Home extends AppCompatActivity {

    private Animation btn_menu = null;
    private final int POSTS_PER_REFRESH = 10;
    private DAOItinerario DAOItinerario;
    private DAOUtente DAOUtente;
    private Future<?> networkRequestFuture;
    private boolean isNotPullRefresh = false;
    private boolean isUpdating = false;
    SwipeRefreshLayout refreshLayout;
    ImageButton btn_utente,btn_home,btn_filtri, add_itin;

    PostAdapter feedPostAdapter;
    RecyclerView RVparent;


    //"LATEST" indica gli ultimi post.
    //"OLDER" è per quando l'utente raggiunge la fine del feed e bisogna caricare altri vecchi post.
    private enum UpdateType{
        LATEST,
        OLDER
    }

    private long oldestLoadedPostId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        add_itin = findViewById(R.id.btn_add);
        btn_home = findViewById(R.id.btn_home);
        btn_utente = findViewById(R.id.btn_utente);
        btn_filtri = findViewById(R.id.btn_filtro);

        btn_menu = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.btn_menu);



        refreshLayout = findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener(){
                    @Override
                    public void onRefresh(){
                        if (isNotPullRefresh) return;
                        updateFeed(UpdateType.LATEST);
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

        add_itin.setOnClickListener(v -> {
            add_itin.animate().rotation(360).withEndAction(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            startActivity(new Intent(Controller_Home.this, ControllerAddItin.class));
                        }
                    });
        });

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_home.startAnimation(btn_menu);
            }
        });

        btn_utente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_utente.animate().rotationY(360).withEndAction(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                startActivity(new Intent(Controller_Home.this, Controller_Utente.class));
                            }
                        });
            }
        });

        btn_filtri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_filtri.animate().rotationY(360).withEndAction(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                startActivity(new Intent(Controller_Home.this, Controller_Ricerca.class));
                            }
                        });
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
                .setAction("Retry", new View.OnClickListener(){
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
        if (updateType.equals(UpdateType.LATEST))
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
                    return getMorePosts(parentItemArrayList);
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
                        else if (updateType.equals(UpdateType.LATEST))
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

    private boolean getMorePosts(List<ParentItem> list) throws WrappedCRUDException{
        List<Itinerario> itinerarioList = DAOItinerario.getLastNItinerarioStartingFrom
                (oldestLoadedPostId-1, POSTS_PER_REFRESH);
        List<ParentItem> parentItemList = new LinkedList<ParentItem>();
        Utente utente;
        int itinerarioCounter = 0;
        for (Itinerario i : itinerarioList){
            utente = DAOUtente.getUtenteByEmail(i.getAuthorId());
            parentItemList.add(itinerarioToParentItem
                    (i, utente.getNome() + " " + utente.getCognome()));
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
        return new ParentItem(itinerario.getNome(),
                itinerario.getDifficoltaItinerario(),
                itinerario.getDurata(),
                itinerario.getNomePuntoIniziale(),
                author);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(networkRequestFuture != null) networkRequestFuture.cancel(true);
    }

}

