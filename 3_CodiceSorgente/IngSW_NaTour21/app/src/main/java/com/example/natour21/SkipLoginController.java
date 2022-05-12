package com.example.natour21;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.natour21.chat.stompclient.UserStompClient;
import com.example.natour21.controller.Controller_Home;
import com.example.natour21.map.MapActivity;
import com.example.natour21.sharedprefs.UserSessionManager;

public class SkipLoginController extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UserSessionManager.getInstance().setKeys("test@test.com", "FAKE");
        UserSessionManager.getInstance().setLoggedIn(true);
        UserStompClient.getInstance().connect();
        Intent intent = new Intent(SkipLoginController.this, Controller_Home.class);
        startActivity(intent);


    }

}
