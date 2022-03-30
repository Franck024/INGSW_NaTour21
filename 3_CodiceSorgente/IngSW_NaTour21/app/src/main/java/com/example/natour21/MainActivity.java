package com.example.natour21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.natour21.controller.ControllerLogin;
import com.example.natour21.controller.Controller_Home;
import com.example.natour21.controller.Controller_Messaggi;
import com.example.natour21.controller.Controller_itinerario;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent page1;
        page1 = new Intent(MainActivity.this, Controller_itinerario.class);
       // page1 = new Intent(MainActivity.this, MapActivity.class);
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