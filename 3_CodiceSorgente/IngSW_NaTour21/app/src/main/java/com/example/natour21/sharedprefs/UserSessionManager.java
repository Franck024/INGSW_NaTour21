package com.example.natour21.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.natour21.exceptions.UninitializedUserSessionException;

public class UserSessionManager {

    private UserSessionManager instance;
    private final SharedPreferences sharedPreferences;
    private static final int SHARED_PREFERENCES_MODE = Context.MODE_PRIVATE;
    private static final String SHARED_PREFERENCES_FILENAME = "NaTour21Preferences";
    private SharedPreferences.Editor editor;
    private UserSessionKeys userSessionKeys;

    private static final String PREF_KEY_USER_ID = "USER_ID";
    private static final String PREF_KEY_LOGIN_STATUS = "LOGIN_STATUS";

    public class UserSessionKeys{

        private final String USER_ID;
        private boolean isLoggedIn;

        public UserSessionKeys(String userId){
            this.USER_ID = userId;
            isLoggedIn = false;
        }

        public String getUserId() {
            return USER_ID;
        }

        protected void setLoggedIn(boolean loggedIn) {
            isLoggedIn = loggedIn;
        }

        public boolean isLoggedIn() {
            return isLoggedIn;
        }
    }

    public void init(Context context){
        instance = new UserSessionManager(context);
    }

    public UserSessionManager getInstance() {
        if (instance == null) throw new UninitializedUserSessionException
                ("Nessuna sessione inizializzata.");
        else return instance;
    }

    private UserSessionManager(Context context){
        sharedPreferences = context.getSharedPreferences
                (SHARED_PREFERENCES_FILENAME, SHARED_PREFERENCES_MODE);
        editor = sharedPreferences.edit();
    }

    public void beginSession(UserSessionKeys userSessionKeys){
        this.userSessionKeys = userSessionKeys;
    }

    public void setLoggedIn(boolean isLoggedIn){
        if (userSessionKeys == null) return;
        userSessionKeys.setLoggedIn(isLoggedIn);
    }

    public boolean saveUserSession(){
        if (userSessionKeys == null) return false;
        editor.putString(PREF_KEY_USER_ID, userSessionKeys.getUserId());
        editor.putBoolean(PREF_KEY_LOGIN_STATUS, userSessionKeys.isLoggedIn());
        return editor.commit();
    }



}
