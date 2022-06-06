package com.example.natour21.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.amplifyframework.rx.RxAmplify;
import com.example.natour21.R;
import com.example.natour21.sharedprefs.UserSessionManager;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        SettingsFragment settingsFragment = new SettingsFragment();
        PreferenceScreen preferenceScreen = settingsFragment.getPreferenceScreen();
        Preference manageUtentePreference =  settingsFragment.findPreference("utenteSettings");
        Preference adminStatsAccessPreference = settingsFragment.findPreference("adminaccess");
        Preference logoutPreference = settingsFragment.findPreference("logout");
        if (!bundle.containsKey("IS_ADMIN") || !bundle.getBoolean("IS_ADMIN")){
            preferenceScreen.removePreference(adminStatsAccessPreference);
        }

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
                Callable<Void> logoutCallable = () -> {
                    UserSessionManager.getInstance().logoutBlocking();
                    return null;
                };
                RxAmplify.Auth.signOut()
                        .andThen((CompletableSource) result -> Completable.fromCallable(logoutCallable))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( () -> startActivity(goBackToLoginIntent),
                                error ->  showUserFriendlyErrorMessageAndLogThrowable("Impossibile eseguire il logout.", error));
                return true;
            }
        });

        if (adminStatsAccessPreference == null) return;

        adminStatsAccessPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                startActivity(new Intent(SettingsActivity.this, Controller_Statistiche.class));
                return true;
            }
        });
    }

    private void showUserFriendlyErrorMessageAndLogThrowable(String s, Throwable throwable){
        if (throwable != null) Log.e("LOGIN", throwable.getMessage(), throwable);
        else Log.e("LOGIN", s);
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            setPreferencesFromResource(R.xml.frame_settings, rootKey);
        }

    }
}
