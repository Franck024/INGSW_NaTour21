package com.example.natour21.controller;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.natour21.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerRegistrazione extends AppCompatActivity {
    private EditText nome, cognome, email, pass, confPass, citta, cell;
    private CheckBox gdpr;
    private Button vPass, visConfPass, btnConfReg;
    private TextView textPolicy, txtCreate;

    int count = 0, ris = 0, counter2 = 0, r2 = 0;
    private Animation anim_btn = null, anim_txtview = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_registrazione);

        nome = findViewById(R.id.editTextNome);
        cognome = findViewById(R.id.editTextCognome);
        email = findViewById(R.id.editTextEmail);
        pass = findViewById(R.id.editTextPassword);
        confPass = findViewById(R.id.editTextConfirmPassword);
        citta = findViewById(R.id.editTextCity);
        cell = findViewById(R.id.editTextPhone);

        textPolicy = findViewById(R.id.textViewPasswordRules);
        txtCreate = findViewById(R.id.textViewCreateAccount);

        btnConfReg = findViewById(R.id.btnConfirm);
        vPass = findViewById(R.id.visPass);
        visConfPass = findViewById(R.id.visConPass);

        gdpr = findViewById(R.id.checkBoxAcceptTerms);

        anim_btn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_bottone);
        anim_txtview = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_textview);

        animazioni();

        //Click
        vPass.setOnClickListener(v -> {
            count++;
            ris = count % 2;
            if (ris != 0) {
                pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                vPass.getBackground().setAlpha(50);
            } else {
                pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                vPass.getBackground().setAlpha(200);
            }
        });

        visConfPass.setOnClickListener(view -> {
            counter2++;
            r2 = counter2 % 2;
            if (r2 != 0) {
                confPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                visConfPass.getBackground().setAlpha(50);
            } else {
                confPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                visConfPass.getBackground().setAlpha(200);
            }
        });

        btnConfReg.setOnClickListener(v -> {
            btnConfReg.startAnimation(anim_btn);
            registerUser();
        });

        nome.setOnClickListener(v -> nome.startAnimation(anim_txtview));
        cognome.setOnClickListener(v -> cognome.startAnimation(anim_txtview));
        email.setOnClickListener(v -> email.startAnimation(anim_txtview));
        pass.setOnClickListener(v -> pass.startAnimation(anim_txtview));
        citta.setOnClickListener(v -> citta.startAnimation(anim_txtview));
        confPass.setOnClickListener(v -> confPass.startAnimation(anim_txtview));
        cell.setOnClickListener(v -> cell.startAnimation(anim_txtview));
    }


    private void animazioni() {
        txtCreate.setTranslationY(1000);
        txtCreate.animate().translationY(0).setDuration(700).setStartDelay(200).start();
        nome.setTranslationX(1000);
        nome.animate().translationX(0).setDuration(700).setStartDelay(600).start();
        cognome.setTranslationX(1000);
        cognome.animate().translationX(0).setDuration(700).setStartDelay(800).start();
        email.setTranslationX(1000);
        email.animate().translationX(0).setDuration(700).setStartDelay(1000).start();
        citta.setTranslationX(1000);
        citta.animate().translationX(0).setDuration(700).setStartDelay(1400).start();
        cell.setTranslationX(1000);
        cell.animate().translationX(0).setDuration(700).setStartDelay(1200).start();
        textPolicy.setTranslationX(1000);
        textPolicy.animate().translationX(0).setDuration(700).setStartDelay(1600).start();
        pass.setTranslationX(1000);
        pass.animate().translationX(0).setDuration(700).setStartDelay(1800).start();
        confPass.setTranslationX(1000);
        confPass.animate().translationX(0).setDuration(700).setStartDelay(2000).start();
        vPass.setTranslationX(1000);
        vPass.animate().translationX(0).setDuration(700).setStartDelay(1800).start();
        visConfPass.setTranslationX(1000);
        visConfPass.animate().translationX(0).setDuration(700).setStartDelay(2000).start();
        gdpr.setTranslationY(1000);
        gdpr.animate().translationY(0).setDuration(700).setStartDelay(1400).start();
        btnConfReg.setTranslationY(1000);
        btnConfReg.animate().translationY(0).setDuration(800).setStartDelay(1800).start();
    }

    private void registerUser() {

        String nameText = nome.getText().toString().trim();
        String surnameText = cognome.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String passwordText = pass.getText().toString().trim();
        String confPasswordText = confPass.getText().toString().trim();
        String cityText = citta.getText().toString().trim();
        String telephoneText = cell.getText().toString().trim();

        if (nameText.isEmpty()) {
            nome.setError("Campo obbligatorio.");
            nome.requestFocus();
            return;
        }

        if (surnameText.isEmpty()) {
            cognome.setError("Campo obbligatorio.");
            cognome.requestFocus();
            return;
        }

        if (emailText.isEmpty()) {
            email.setError("Campo obbligatorio.");
            email.requestFocus();
            return;
        }

        if (passwordText.isEmpty()) {
            pass.setError("Campo obbligatorio.");
            pass.requestFocus();
            return;
        }

        if (confPasswordText.isEmpty()) {
            confPass.setError("Campo obbligatorio.");
            confPass.requestFocus();
            return;
        }

        if (!verificaPass(passwordText)) {
            pass.setError("La password non rispetta le policy.");
            pass.requestFocus();
            return;
        }

        if (!(passwordText.equals(confPasswordText))) {
            pass.setError("Le password non corrispondono.");
            pass.requestFocus();

            confPass.setError("Le password non corrispondono.");
            confPass.requestFocus();

            return;
        }

        if (!(verificaEmail(emailText))) {
            email.setError("Email non valida.");
            email.requestFocus();
            return;
        }

        if (!(gdpr.isChecked())) {
            Toast.makeText(ControllerRegistrazione.this,
                    "Accettare i termini per continuare.",
                    Toast.LENGTH_LONG).show();
        }
        // *** INVIARE I DATI E SALVARLI  *** //

    }


    private boolean verificaEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean verificaPass(String password) {
        Pattern path = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!?&£^*ç@#$%]).{8,15})");
        Matcher m = path.matcher(password);
        return m.matches();
    }
}

