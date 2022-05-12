package com.example.natour21.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.natour21.R;
import com.example.natour21.sharedprefs.UserSessionManager;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsFragment settingsFragment = new SettingsFragment();
        Preference manageUtentePreference =  settingsFragment.findPreference("utenteSettings");
        Preference logoutPreference = settingsFragment.findPreference("logout");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settingsFrameLayout, settingsFragment)
                .commit();

        manageUtentePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                Toast.makeText(getApplicationContext(),
                        "Questa funzionalità arriverà in futuro.",
                        Toast.LENGTH_SHORT);
                return true;
            }
        });

        logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                Intent goBackToLoginIntent = new Intent
                        (SettingsActivity.this, ControllerLogin.class);
                Observable.just(UserSessionManager.getInstance().logoutBlocking())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(hasLoggedOut -> {
                            if (!hasLoggedOut){
                                Toast.makeText(getApplicationContext(),
                                        "Impossibile eseguire il logout.",
                                        Toast.LENGTH_SHORT);
                            }
                            else if (hasLoggedOut) startActivity(goBackToLoginIntent);
                        });
                ;
                return true;
            }
        });
    }

    private class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            setPreferencesFromResource(R.xml.frame_settings, rootKey);
        }

    }
}
