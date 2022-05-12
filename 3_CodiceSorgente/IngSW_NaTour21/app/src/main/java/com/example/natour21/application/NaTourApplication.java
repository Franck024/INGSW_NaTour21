package com.example.natour21.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.amplifyframework.*;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.rx.RxAmplify;
import com.example.natour21.chat.stompclient.UserStompClient;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.sharedprefs.UserSessionManager;

public class NaTourApplication extends Application implements
        LifecycleEventObserver {

    public void onCreate(){
        super.onCreate();

        try {
            RxAmplify.addPlugin(new AWSCognitoAuthPlugin());
            RxAmplify.configure(getApplicationContext());
            Log.i("NaTourApplication", "Initialized Amplify");
            UserSessionManager.init(getApplicationContext());
            Log.i("NaTourApplication", "Initialized UserSessionManager");
            UserStompClient.init(this);
            Log.i("NaTourApplication", "Initialized UserStompClient");
            ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        } catch (AmplifyException error) {
            Log.e("NaTourApplication", "Could not initialize Amplify", error);
        }
        catch (InvalidConnectionSettingsException icse){
            Log.e("NaTourApplication", "Could not initialize stomp client!", icse);
        }



    }


    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (isAppInForeground(event)) onAppToForeground();
        else if (isAppInBackground(event)) onAppToBackground();

    }

    private boolean isAppInForeground(@NonNull Lifecycle.Event event){
        if (event.equals(Lifecycle.Event.ON_START) &&
                ProcessLifecycleOwner
                        .get()
                        .getLifecycle()
                        .getCurrentState()
                        .isAtLeast(Lifecycle.State.STARTED))
            return true;
        else return false;
    }

    private boolean isAppInBackground(@NonNull Lifecycle.Event event){
        if (event.equals(Lifecycle.Event.ON_STOP) &&
                ProcessLifecycleOwner
                        .get()
                        .getLifecycle()
                        .getCurrentState()
                        .equals(Lifecycle.State.CREATED))
            return true;
        else return false;
    }

    private void onAppToForeground(){
        if (UserStompClient.getInstance().isConnected()) return;
        UserStompClient.getInstance().connect();
    }

    private void onAppToBackground(){
        if (!(UserStompClient.getInstance().isConnected())) return;
        UserStompClient.getInstance().disconnect();
    }
}
