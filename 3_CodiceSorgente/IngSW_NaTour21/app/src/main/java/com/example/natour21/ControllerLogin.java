package com.example.natour21;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.natour21.entities.Messaggio;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.OffsetDateTime;

import retrofit2.converter.jackson.JacksonConverterFactory;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ControllerLogin extends AppCompatActivity {
    private EditText email, password;
    private TextView registrazione, pass_dim, txt_reg;
    private Button login, fb, google, eye;
    private Animation anim_btn = null, anim_txtview = null;
    private int counter=0, r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_login);

        email = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        registrazione = findViewById(R.id.textViewSignUp);
        pass_dim = findViewById(R.id.textViewPasswordRecovery);
        login = findViewById(R.id.buttonLogin);
        fb = findViewById(R.id.btnFbLogin);
        google = findViewById(R.id.btnGoogleLogin);
        eye = findViewById(R.id.eye);
        txt_reg = findViewById(R.id.textViewRegister);

        anim_btn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_bottone);
        anim_txtview = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_textview);

        animazioni();

        eye.setOnClickListener(v -> {
            counter++;
            r= counter %2;
            if(r !=0 ){
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                eye.getBackground().setAlpha(50);
            }else{
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eye.getBackground().setAlpha(200);
            }
        });

        //Click TextView
        email.setOnClickListener(v -> email.startAnimation(anim_txtview));
        password.setOnClickListener(v -> password.startAnimation(anim_txtview));

        //Click EditView
        registrazione.setOnClickListener(view -> startActivity(new Intent(ControllerLogin.this, ControllerRegistrazione.class)));
        pass_dim.setOnClickListener(view -> startActivity(new Intent(ControllerLogin.this, ControllerRecovery.class)));

        //Click bottoni
        login.setOnClickListener(v -> {
            login.startAnimation(anim_btn);
            startActivity(new Intent(ControllerLogin.this, Controller_Home.class));
        });
        fb.setOnClickListener(v -> fb.startAnimation(anim_btn));
        google.setOnClickListener(v -> google.startAnimation(anim_btn));

    }

    private void animazioni(){
        email.setTranslationX(1000);
        email.animate().translationX(0).setDuration(800).setStartDelay(400).start();
        password.setTranslationX(1000);
        password.animate().translationX(0).setDuration(800).setStartDelay(600).start();
        login.setTranslationX(1000);
        login.animate().translationX(0).setDuration(800).setStartDelay(800).start();
        pass_dim.setTranslationX(1000);
        pass_dim.animate().translationX(0).setDuration(800).setStartDelay(1000).start();
        eye.setTranslationX(1000);
        eye.animate().translationX(0).setDuration(1000).setStartDelay(600).start();
        fb.setTranslationY(1000);
        fb.animate().translationY(0).setDuration(800).setStartDelay(400).start();
        google.setTranslationY(1000);
        google.animate().translationY(0).setDuration(800).setStartDelay(600).start();
        txt_reg.setTranslationY(1000);
        txt_reg.animate().translationY(0).setDuration(800).setStartDelay(800).start();
        registrazione.setTranslationY(1000);
        registrazione.animate().translationY(0).setDuration(800).setStartDelay(1000).start();

    }

}
