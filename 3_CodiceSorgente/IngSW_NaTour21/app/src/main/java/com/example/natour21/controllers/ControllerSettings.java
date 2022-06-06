package com.example.natour21.controllers;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natour21.R;

public class ControllerSettings extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        SettingsFragment settingsFragment = new SettingsFragment();

        if (bundle == null || !bundle.containsKey("IS_ADMIN")
                || !bundle.getBoolean("IS_ADMIN")){
            settingsFragment.setIsUtenteNotAdmin(true);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settingsFrameLayout, settingsFragment)
                .commit();
    }

}
