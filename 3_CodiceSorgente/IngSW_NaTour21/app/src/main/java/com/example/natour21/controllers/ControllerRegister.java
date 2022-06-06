package com.example.natour21.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import androidx.fragment.app.DialogFragment;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.auth.result.step.AuthNextSignUpStep;
import com.amplifyframework.auth.result.step.AuthSignUpStep;
import com.amplifyframework.rx.RxAmplify;
import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.R;
import com.example.natour21.entities.Utente;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.sharedprefs.UserSessionManager;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ControllerRegistrazione extends AppCompatActivity implements ConfirmationCodeDialogFragment.OnSuccessfulCodeConfirmationDismissListener {
    private EditText nome, cognome, email, pass, confPass, citta, cell;
    private CheckBox gdpr;
    private Button vPass, visConfPass, btnConfReg;
    private TextView textPolicy, txtCreate;

    int count = 0, ris = 0, counter2 = 0, r2 = 0;
    private Animation anim_btn = null, anim_txtview = null;

    private DAOUtente DAOUtente;
    private Utente utente;
    private String federatedAccountEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_registrazione);

        try{
            DAOUtente = DAOFactory.getDAOUtente();
        }
        catch (InvalidConnectionSettingsException icse){
            showUserFriendlyErrorMessageAndLogThrowable("Errore. Riavviare l'applicazione.", icse);
        }


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

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.containsKey("FEDERATED_ACCOUNT_EMAIL")){
            federatedAccountEmail = bundle.getString("FEDERATED_ACCOUNT_EMAIL");
            if (federatedAccountEmail.isEmpty()) return;
            email.setVisibility(View.GONE);
            pass.setVisibility(View.GONE);
            confPass.setVisibility(View.GONE);
            textPolicy.setVisibility(View.GONE);
            vPass.setVisibility(View.GONE);
            visConfPass.setVisibility(View.GONE);
        }

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




    private void registerUser() {
        String nameText = nome.getText().toString().trim();
        String surnameText = cognome.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String passwordText = pass.getText().toString().trim();
        String confPasswordText = confPass.getText().toString().trim();
        String cityText = citta.getText().toString().trim();
        String telephoneText = cell.getText().toString().trim();
        if (cityText.equals("")) cityText = null;
        if (telephoneText.equals("")) telephoneText = null;

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
            return;
        }
        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), emailText)
                .build();
        utente = new Utente(emailText, nameText, surnameText,telephoneText,cityText, false);
        RxAmplify.Auth.signUp(emailText, passwordText, options).subscribe(
                result -> onSignUpResult(result, utente),
                error -> runOnUiThread(() -> onSignUpError(error)));

    }

    private void registerFederatedUser(){
        String nameText = nome.getText().toString().trim();
        String surnameText = cognome.getText().toString().trim();
        String cityText = citta.getText().toString().trim();
        String telephoneText = cell.getText().toString().trim();
        if (cityText.equals("")) cityText = null;
        if (telephoneText.equals("")) telephoneText = null;

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

        if (!(gdpr.isChecked())) {
            Toast.makeText(ControllerRegistrazione.this,
                    "Accettare i termini per continuare.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        utente = new Utente(federatedAccountEmail, nameText, surnameText, telephoneText,cityText, false);
        Observable.fromCallable(() -> DAOUtente.insertUtente(utente))
                .flatMap(isUtenteInserted -> {
                    if (!isUtenteInserted) throw new Exception("Impossibile inserire l'utente.");
                    UserSessionManager.getInstance().setKeys(utente.getEmail());
                    UserSessionManager.getInstance().setLoggedIn(true);
                    return Observable.just(UserSessionManager.getInstance().saveUserSessionBlocking());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(finalResult ->
                {
                    Toast.makeText(getApplicationContext(), "Registrazione completata!", Toast.LENGTH_LONG);
                    startActivity(new Intent(ControllerRegistrazione.this, Controller_Home.class ));
                }, error -> showUserFriendlyErrorMessageAndLogThrowable("Errore nella registrazione.", error));


    }

    private void onSignUpResult(AuthSignUpResult result, Utente utente){
        String email = utente.getEmail();
        if (result.getNextStep().getSignUpStep().equals(AuthSignUpStep.CONFIRM_SIGN_UP_STEP)){
            this.utente = utente;
            DialogFragment confirmationCodeDialogFragment =
                    new ConfirmationCodeDialogFragment(utente.getEmail(), this);
            confirmationCodeDialogFragment.show(getSupportFragmentManager(), "confirmationcode");
        }
        else if (result.getNextStep().getSignUpStep().equals(AuthSignUpStep.DONE)){
            Callable<Boolean> doesUtenteExistInBackendCallable = () -> DAOUtente.getUtenteByEmail(email) != null;
            Callable<Boolean> insertUtenteInBackEndCallable = () -> DAOUtente.insertUtente(utente);
            Observable.fromCallable(doesUtenteExistInBackendCallable)
                    .flatMap(doesUtenteExist -> {
                        if (doesUtenteExist) return Observable.just(true);
                        else return Observable.fromCallable(insertUtenteInBackEndCallable);
                    })
                    .flatMap(isUtenteInserted -> {
                        if (!isUtenteInserted) throw new Exception("Impossibile inserire l'utente.");
                        UserSessionManager.getInstance().setKeys(utente.getEmail());
                        UserSessionManager.getInstance().setLoggedIn(true);
                        return Observable.just(UserSessionManager.getInstance().saveUserSessionBlocking());
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(finalResult ->
                            {
                                Toast.makeText(getApplicationContext(), "Registrazione completata!", Toast.LENGTH_LONG);
                                startActivity(new Intent(ControllerRegistrazione.this, Controller_Home.class ));
                            }, error -> showUserFriendlyErrorMessageAndLogThrowable("Errore nella registrazione.", error));

        }

    }

    private void onSignUpError(Throwable error){
        if ((error instanceof AuthException.UsernameExistsException)
                || (error instanceof AuthException.AliasExistsException))
            showUserFriendlyErrorMessageAndLogThrowable("Un utente con la stessa email esiste già.", error);
        else showUserFriendlyErrorMessageAndLogThrowable("Impossibile eseguire la registrazione.", error);
    }

    private void showUserFriendlyErrorMessageAndLogThrowable(String s, Throwable throwable){
        if (throwable != null) Log.e("LOGIN", throwable.getMessage(), throwable);
        else Log.e("LOGIN", s);
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    private boolean verificaEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean verificaPass(String password) {
        Pattern path = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!?&£^*ç@#$%]).{8,15})");
        Matcher m = path.matcher(password);
        return m.matches();
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


    @Override
    public void onSuccessfulCodeConfirmationDismiss(AuthSignUpResult authSignUpResult) {
        onSignUpResult(authSignUpResult, utente);
    }
}

