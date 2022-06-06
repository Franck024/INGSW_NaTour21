package com.example.natour21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amplifyframework.rx.RxAmplify;
import com.example.natour21.controllers.ControllerLogin;
import com.example.natour21.controllers.ControllerHome;
import com.example.natour21.sharedprefs.UserSessionManager;


public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent loginIntent = new Intent(StartupActivity.this, ControllerLogin.class);
        Intent skipLoginIntent = new Intent(StartupActivity.this, ControllerHome.class);

        UserSessionManager userSessionManager = UserSessionManager.getInstance();

        if (userSessionManager.isLoggedIn()){
            RxAmplify.Auth.fetchAuthSession().subscribe(authSession -> {
                if (authSession.isSignedIn()) startActivity(skipLoginIntent);
                else startActivity(loginIntent);
            }, error -> {
                Log.e("STARTUPACTIVITY", error.getMessage(), error);
                finishAndRemoveTask();
            });
        } else startActivity(loginIntent);

    }

}