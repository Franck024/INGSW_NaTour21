package com.example.natour21.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.natour21.exceptions.UninitializedUserSessionException;

public class UserSessionManager {

    private static UserSessionManager instance;
    private final SharedPreferences sharedPreferences;
    private static final int SHARED_PREFERENCES_MODE = Context.MODE_PRIVATE;
    private static final String SHARED_PREFERENCES_FILENAME = "NaTour21Preferences";
    private UserSessionKeys userSessionKeys;

    private static final String PREF_KEY_USER_ID = "USER_ID";
    private static final String PREF_KEY_LOGIN_STATUS = "LOGIN_STATUS";
    private static final String PREF_KEY_TOKEN = "TOKEN";

    public class UserSessionKeys{

        private final String USER_ID;
        private boolean isLoggedIn;
        private String token;

        public UserSessionKeys(String userId, String token){
            this.USER_ID = userId;
            this.token = token;
            isLoggedIn = false;
        }

        public String getUserId() {
            return USER_ID;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        protected void setLoggedIn(boolean loggedIn) {
            isLoggedIn = loggedIn;
        }

        public boolean isLoggedIn() {
            return isLoggedIn;
        }
    }

    public static void init(Context context){
        instance = new UserSessionManager(context);
    }

    public static UserSessionManager getInstance() {
        if (instance == null) throw new UninitializedUserSessionException
                ("Nessuna sessione inizializzata.");
        else return instance;
    }

    private UserSessionManager(Context context){
        sharedPreferences = context.getSharedPreferences
                (SHARED_PREFERENCES_FILENAME, SHARED_PREFERENCES_MODE);
        String userId = sharedPreferences.getString(PREF_KEY_USER_ID, null);
        String token = sharedPreferences.getString(PREF_KEY_TOKEN, null);
        Boolean isLoggedIn = sharedPreferences.getBoolean(PREF_KEY_LOGIN_STATUS, false);

        if (userId == null || token == null) return;
        setKeys(userId, token);
        setLoggedIn(isLoggedIn);
    }

    public void setKeys(String userId, String token){
        this.userSessionKeys = new UserSessionKeys(userId, token);
    }

    public void setLoggedIn(boolean isLoggedIn){
        if (userSessionKeys == null) return;
        userSessionKeys.setLoggedIn(isLoggedIn);
    }

    public boolean saveUserSessionBlocking(){
        if (userSessionKeys == null) return false;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_USER_ID, userSessionKeys.getUserId());
        editor.putBoolean(PREF_KEY_LOGIN_STATUS, userSessionKeys.isLoggedIn());
        editor.putString(PREF_KEY_TOKEN, userSessionKeys.getToken());
        return editor.commit();
    }

    public String getUserId(){
        if (userSessionKeys == null) return null;
        else return userSessionKeys.getUserId();
    }

    public String getToken(){
        if (userSessionKeys == null) return null;
        else return userSessionKeys.getToken();
    }

    public boolean isLoggedIn(){
        if (userSessionKeys == null) return false;
        else return userSessionKeys.isLoggedIn();
    }

    public boolean logoutBlocking(){
        if (userSessionKeys == null) return false;
        setLoggedIn(false);
        return clearTokenBlocking();
    }

    private boolean clearTokenBlocking(){
        if (userSessionKeys == null) return false;
        userSessionKeys.setToken(null);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_TOKEN, null);
        return editor.commit();
    }


}
