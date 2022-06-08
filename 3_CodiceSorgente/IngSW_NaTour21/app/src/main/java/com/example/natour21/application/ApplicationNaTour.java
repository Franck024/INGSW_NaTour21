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
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;

public class ApplicationNaTour extends Application implements
        Application.ActivityLifecycleCallbacks, Configuration.Provider {

    private int activityCounter = 0;

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
            registerActivityLifecycleCallbacks(this);
            org.osmdroid.config.Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        } catch (AmplifyException error) {
            Log.e("NaTourApplication", "Could not initialize Amplify", error);
        }
        catch (InvalidConnectionSettingsException icse){
            Log.e("NaTourApplication", "Could not initialize stomp client!", icse);
        }
        NotificationUtils.createNotificationChannel(this);
    }





    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        activityCounter++;
        if (!UserStompClient.getInstance().isConnected() && activityCounter > 0)
            UserStompClient.getInstance().connect();
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activityCounter--;
        if (UserStompClient.getInstance().isConnected() && activityCounter == 0)
            UserStompClient.getInstance().disconnect();
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
