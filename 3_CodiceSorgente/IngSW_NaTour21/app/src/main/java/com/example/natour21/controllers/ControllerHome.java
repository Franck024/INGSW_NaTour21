package com.example.natour21.controllers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.natour21.post.AdapterPost;
import com.example.natour21.post.Post;
import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOItinerario;
import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.R;
import com.example.natour21.chat.stompclient.UserStompClient;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.entities.Utente;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.exceptions.WrappedCRUDException;
import com.example.natour21.sharedprefs.UserSessionManager;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ControllerHome extends AppCompatActivity implements java.util.Observer {

    private ImageButton imageBtnAddItinerario, imageBtnSettings, imageBtnProfile, imageBtnHome,
            imageBtnSearch, imageBtnInbox;
    private TextView textViewUnreadMessaggioCount;


    //Numero di post da ottenere per aggiornamento del feed.
    private final int POSTS_PER_REFRESH = 10;
    private boolean isUpdating = false;

    private DAOItinerario DAOItinerario;
    private DAOUtente DAOUtente;

    private SwipeRefreshLayout refreshLayout;
    //boolean che segna se è stato eseguito un refresh attraverso lo swipeRefreshLayout.
    private boolean isNotPullRefresh = false;

    private AdapterPost adapterPostFeed;
    private RecyclerView recyclerViewFeed;

    //"LATEST" indica gli ultimi post quando il feed è vuoto.
    //"NEWER" indica i post più nuovi dell'ultimo post caricato, quando l'utente richiede un pull refresh.
    //"OLDER" è per quando l'utente raggiunge la fine del feed e bisogna caricare altri vecchi post.
    private enum UpdateType{
        LATEST,
        NEWER,
        OLDER
    }

    //Usati per le query su quali post ottenere in caso di richiesta di aggiornamento.
    private long oldestLoadedPostId = 0;
    private long newestLoadedPostId = 0;

    private FirebaseAnalytics firebaseAnalyticsInstance;

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        UserStompClient.getInstance().addObserver(this);

        imageBtnAddItinerario = findViewById(R.id.imageBtnAddItinerario);
        imageBtnSettings = findViewById(R.id.imageBtnSettings);
        imageBtnHome = findViewById(R.id.imageBtnHome);
        imageBtnProfile = findViewById(R.id.imageBtnProfile);
        imageBtnSearch = findViewById(R.id.imageBtnSearch);
        imageBtnInbox = findViewById(R.id.imageBtnInbox);
        textViewUnreadMessaggioCount = findViewById(R.id.textViewUnreadMessaggioCount);

        imageBtnSettings.setOnClickListener(view -> {
            startActivity(new Intent(ControllerHome.this, ControllerSettings.class));
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
        recyclerViewFeed = findViewById(R.id.recyclerViewFeed);

        try{
            DAOItinerario = DAOFactory.getDAOItinerario();
            DAOUtente = DAOFactory.getDAOUtente();
        }
        catch (InvalidConnectionSettingsException icse){
            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable(getApplicationContext(), "Home",
                    "Impossibile aprire l'home.", icse);
        }
        firebaseAnalyticsInstance = FirebaseAnalytics.getInstance(ControllerHome.this);

        AdapterPost.OnPostClickListener onPostClickListener = p -> {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Long.toString(p.getIdItinerario()));
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "itinerario_home");
            firebaseAnalyticsInstance.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        };
        adapterPostFeed = new AdapterPost(new ArrayList<Post>(), onPostClickListener);
        recyclerViewFeed.setAdapter(adapterPostFeed);
        isNotPullRefresh = true;
        refreshLayout.setRefreshing(true);
        updateFeed(UpdateType.LATEST);
        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        Callable<Long> getUnreadMessageCountCallable = () ->
                UserStompClient.getInstance().getUnreadMessageCountBlocking();

        Observable.fromCallable(getUnreadMessageCountCallable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result < 1) {
                        textViewUnreadMessaggioCount.setVisibility(View.INVISIBLE);
                        return;
                    }
                    textViewUnreadMessaggioCount.setText
                            ((result < 100) ? Math.toIntExact(result) + "" : "99+");
                    textViewUnreadMessaggioCount.setVisibility(View.VISIBLE);
                });

        imageBtnAddItinerario.setOnClickListener(v -> {
            imageBtnAddItinerario.animate().rotation(360).withEndAction(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            startActivity(new Intent(ControllerHome.this, ControllerAddNewItinerario.class));
                        }
                    });
        });

        imageBtnInbox.setOnClickListener(v -> {
            imageBtnInbox.animate().rotation(360).withEndAction(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            startActivity(new Intent(ControllerHome.this, ControllerInbox.class));
                        }
                    });
        });


        imageBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageBtnProfile.animate().rotationY(360).withEndAction(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Intent profileIntent = new Intent(ControllerHome.this, ControllerProfile.class);
                                profileIntent.putExtra("USER_ID", UserSessionManager.getInstance().getUserId());
                                startActivity(profileIntent);
                            }
                        });
            }
        });

        imageBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageBtnSearch.animate().rotationY(360).withEndAction(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                startActivity(new Intent(ControllerHome.this, ControllerSearchItinerario.class));
                            }
                        });
            }
        });
    }

    private void onUpdateError(Throwable throwable, UpdateType updateType){
        String errorMessage = "Impossibile aggiornare il feed.";
        Log.e("Home", throwable.getMessage(), throwable);
        Snackbar.make(
                recyclerViewFeed,
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
        ArrayList<Post> postArrayList = new ArrayList<>();
        if (updateType.equals(UpdateType.LATEST)){
            callable = new Callable<Boolean>(){
                @Override
                public Boolean call() throws Exception{
                    return getLatestPosts(postArrayList);
                }
            };
        }
        else if (updateType.equals(UpdateType.OLDER)){
            callable = new Callable<Boolean>(){
                @Override
                public Boolean call() throws Exception{
                    return getOlderPosts(postArrayList);
                }
            };
        }
        else if (updateType.equals(UpdateType.NEWER)){
            callable = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return getNewerPosts(postArrayList);
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
                            adapterPostFeed.addAll(postArrayList);
                        else if (updateType.equals(UpdateType.LATEST) || updateType.equals(UpdateType.NEWER))
                            adapterPostFeed.addAllAtIndex(0, postArrayList);

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
        if (textViewUnreadMessaggioCount == null) return;
        Long count = (Long) o;
        runOnUiThread(() -> {
            if (count < 1) {
                textViewUnreadMessaggioCount.setVisibility(View.INVISIBLE);
                return;
            }
            textViewUnreadMessaggioCount.setText
                    ((count < 100) ? Math.toIntExact(count) + "" : "99+");
            textViewUnreadMessaggioCount.setVisibility(View.VISIBLE);
        });
    }

    private boolean getNewerPosts(List<Post> list) throws WrappedCRUDException{
        List<Itinerario> itinerarioList = DAOItinerario.getLastNItinerarioNewerThan
                (newestLoadedPostId, POSTS_PER_REFRESH);
        List<Post> postList = new LinkedList<>();
        Utente utente;
        int itinerarioCounter = 0;
        for (Itinerario i : itinerarioList){
            if (itinerarioCounter == 0) newestLoadedPostId = i.getId();
            utente = DAOUtente.getUtenteByEmail(i.getAuthorId());
            postList.add(new Post(i, utente.getDisplayName()));
            itinerarioCounter++;
        }
        return list.addAll(postList);
    }

    private boolean getOlderPosts(List<Post> list) throws WrappedCRUDException{
        List<Itinerario> itinerarioList = DAOItinerario.getLastNItinerarioStartingFrom
                (oldestLoadedPostId-1, POSTS_PER_REFRESH);
        List<Post> postList = new LinkedList<>();
        Utente utente;
        int itinerarioCounter = 0;
        for (Itinerario i : itinerarioList){
            utente = DAOUtente.getUtenteByEmail(i.getAuthorId());
            postList.add(new Post(i, utente.getDisplayName()));
            itinerarioCounter++;
            if (itinerarioCounter == itinerarioList.size())
                oldestLoadedPostId = i.getId();
        }
        return list.addAll(postList);
    }

    private boolean getLatestPosts(List<Post> list) throws WrappedCRUDException {

        List<Itinerario> latestItinerario = DAOItinerario.getLastNItinerario(POSTS_PER_REFRESH);
        List<Post> postList = new LinkedList<>();
        Utente utente;
        int itinerarioCounter = 0;
        for (Itinerario i : latestItinerario){
            if (itinerarioCounter == 0) newestLoadedPostId = i.getId();
            utente = DAOUtente.getUtenteByEmail(i.getAuthorId());
            postList.add(new Post(i, utente.getDisplayName()));
            itinerarioCounter++;
            if (itinerarioCounter == latestItinerario.size())
                oldestLoadedPostId = i.getId();
        }
        return list.addAll(0, postList);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        UserStompClient.getInstance().deleteObserver(this);
    }

}

