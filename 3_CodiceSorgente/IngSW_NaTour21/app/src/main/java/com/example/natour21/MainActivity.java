package com.example.natour21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.rx.RxAmplify;
import com.example.natour21.DAOHTTP.DAOHTTPItinerario;
import com.example.natour21.DAOHTTP.DAOHTTPUtente;
import com.example.natour21.DAOs.DAOItinerario;
import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.entities.Utente;
import com.example.natour21.enums.DifficoltaItinerario;
import com.example.natour21.exceptions.WrappedCRUDException;
import com.example.natour21.map.MapActivity;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent page1;
        //page1 = new Intent(MainActivity.this, ControllerLogin.class);
        page1 = new Intent(MainActivity.this, MapActivity.class);
        startActivity(page1);



        //Test di una chiamata API con retrofit.
        /*DAOItinerario = new DAOHTTPItinerario("http://example.dom4in:port);
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    DAOItinerario.insertItinerario(new Itinerario(7, "testAndroid@test.com", "test", "test", 10, DifficoltaItinerario.facile, "10"));
                    System.out.println("Success");
                }
                catch (WrappedCRUDException wcrude){
                    Log.w("sus", wcrude.getWrappedException().getMessage());
                }
                finally{

                }
            }
        });

        thread.start();*/


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