package com.example.natour21.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amplifyframework.rx.RxAmplify;
import com.example.natour21.chat.room.ChatDatabase;
import com.example.natour21.sharedprefs.UserSessionManager;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ControllerStartup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent loginIntent = new Intent(ControllerStartup.this, ControllerLogin.class);
        Intent skipLoginIntent = new Intent(ControllerStartup.this, ControllerHome.class);

        UserSessionManager userSessionManager = UserSessionManager.getInstance();

        if (userSessionManager.isLoggedIn()){
            RxAmplify.Auth.fetchAuthSession()
                    .flatMap(authSession -> {
                        if (authSession.isSignedIn()) return  Single.just(true);
                        else return Single.fromCallable(() -> {
                            userSessionManager.setLoggedIn(false);
                            getApplicationContext().deleteDatabase(ChatDatabase.getDatabaseName());
                            return false;
                        });
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(shouldSkipLogin -> {
                        if (shouldSkipLogin)  startActivity(skipLoginIntent);
                        else startActivity(loginIntent);
                    }, error -> {
                        Log.e("STARTUPACTIVITY", error.getMessage(), error);
                        Toast.makeText(getApplicationContext(), "Impossibile avviare l'applicazione.", Toast.LENGTH_SHORT)
                                .show();
                        finishAndRemoveTask();
                    });
        } else startActivity(loginIntent);

    }

}