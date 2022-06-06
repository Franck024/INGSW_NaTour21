package com.example.natour21.application;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.os.Build;
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
import androidx.work.Configuration;

import com.amplifyframework.*;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.rx.RxAmplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.natour21.BuildConfig;
import com.example.natour21.R;
import com.example.natour21.chat.stompclient.UserStompClient;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.notification.NotificationUtils;
import com.example.natour21.sharedprefs.UserSessionManager;

public class ApplicationNaTour extends Application implements
        LifecycleEventObserver, Configuration.Provider {


    public void onCreate(){
        super.onCreate();
        try {
            RxAmplify.addPlugin(new AWSCognitoAuthPlugin());
            RxAmplify.addPlugin(new AWSS3StoragePlugin());
            RxAmplify.configure(getApplicationContext());
            Log.i("NaTourApplication", "Initialized Amplify");
            UserSessionManager.init(getApplicationContext());
            Log.i("NaTourApplication", "Initialized UserSessionManager");
            UserStompClient.init(this);
            Log.i("NaTourApplication", "Initialized UserStompClient");
            ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
            org.osmdroid.config.Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        } catch (AmplifyException error) {
            Log.e("NaTourApplication", "Could not initialize Amplify", error);
        }
        catch (InvalidConnectionSettingsException icse){
            Log.e("NaTourApplication", "Could not initialize stomp client!", icse);
        }
        NotificationUtils.createNotificationChannel(this);
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

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();
    }
}
