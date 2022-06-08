package com.example.natour21.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.rx.RxAmplify;
import com.example.natour21.chat.room.ChatDatabase;
import com.example.natour21.chat.stompclient.UserStompClient;
import com.example.natour21.sharedprefs.UserSessionManager;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ControllerStartup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent loginIntent = new Intent(ControllerStartup.this, ControllerLogin.class);
        Intent skipLoginIntent = new Intent(ControllerStartup.this, ControllerHome.class);

        UserSessionManager userSessionManager = UserSessionManager.getInstance();

        AWSMobileClient mobileClient =
                (AWSMobileClient) Amplify.Auth.getPlugin("awsCognitoAuthPlugin").getEscapeHatch();

        if (userSessionManager.isLoggedIn()){
            if (mobileClient.isSignedIn()) {
                UserStompClient.getInstance().setEnabled(true);
                startActivity(skipLoginIntent);
            }
            else
                Completable.fromCallable(() -> {
                    userSessionManager.setLoggedIn(false);
                    if (getApplicationContext().deleteDatabase(ChatDatabase.getDatabaseName()))
                        throw new RuntimeException("Impossibile cancellare il database.");
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {},
                        error -> {
                            Log.e("STARTUPACTIVITY", error.getMessage(), error);
                            Toast.makeText(getApplicationContext(), "Impossibile avviare l'applicazione.", Toast.LENGTH_SHORT)
                                    .show();
                            finishAffinity();
                        });
        } else startActivity(loginIntent);

    }

}