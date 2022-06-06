package com.example.natour21.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.amplifyframework.rx.RxAmplify;
import com.example.natour21.R;
import com.example.natour21.chat.room.ChatDatabase;
import com.example.natour21.sharedprefs.UserSessionManager;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsFragment extends PreferenceFragmentCompat {

    private boolean isUtenteNotAdmin;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.frame_settings, rootKey);
        Preference manageUtentePreference = findPreference("utenteSettings");
        Preference adminStatsAccessPreference = findPreference("adminaccess");
        Preference logoutPreference = findPreference("logout");

        manageUtentePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Questa funzionalità arriverà in futuro.",
                        Toast.LENGTH_SHORT);
                return true;
            }
        });

        logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                Intent goBackToLoginIntent = new Intent
                        (getActivity(), ControllerLogin.class);
                Callable<Void> logoutCallable = () -> {
                    UserSessionManager.getInstance().logoutBlocking();
                    getActivity().deleteDatabase(ChatDatabase.getDatabaseName());
                    return null;
                };
                RxAmplify.Auth.signOut()
                        .andThen((CompletableSource) result -> Completable.fromCallable(logoutCallable))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> startActivity(goBackToLoginIntent),
                                error -> ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                                        (getActivity().getApplicationContext(), "Settings", "Impossibile eseguire il logout.", error));
                return true;
            }
        });

        if (adminStatsAccessPreference == null) return;

        adminStatsAccessPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                startActivity(new Intent(getActivity(), ControllerStatistiche.class));
                return true;
            }
        });
    }

    public void setIsUtenteNotAdmin(boolean isUtenteNotAdmin) {
        this.isUtenteNotAdmin = isUtenteNotAdmin;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isUtenteNotAdmin) return;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference preferenceAdminAccess = findPreference("adminaccess");
        if (preferenceAdminAccess == null) return;
        preferenceScreen.removePreference(preferenceAdminAccess);
    }
}
