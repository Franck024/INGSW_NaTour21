package com.example.natour21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.rx.RxAmplify;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent page1 = new Intent(MainActivity.this, ControllerLogin.class);
        startActivity(page1);


        //Test fetching dell'auth session
        /*RxAmplify.Auth.fetchAuthSession()
                .subscribe(
                        result -> Log.i("AmplifyQuickstart", result.toString()),
                        error -> Log.e("AmplifyQuickstart", error.toString())
                );*/

        //Test SignUp
        //Il codice di conferma è valido per 24 ore.
        //ATTENZIONE: Usare email reali per evitare limitazioni da Amazon

        /*String email = "";
        String password = "testPass";
        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), email)
                .build();
        RxAmplify.Auth.signUp(email, password, options).subscribe(
                result -> Log.i("AmplifyQuickstart", result.toString()),
                error -> Log.e("AmplifyQuickstart", error.toString())
        );;*/



    }
}