package com.example.natour21;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.*;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.rx.RxAmplify;

public class NaTourApplication extends Application {
    public void onCreate(){
        super.onCreate();

        try {
            RxAmplify.addPlugin(new AWSCognitoAuthPlugin());
            RxAmplify.configure(getApplicationContext());
            Log.i("NaTourApplication", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("NaTourApplication", "Could not initialize Amplify", error);
        }

    }

}
