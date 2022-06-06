package com.example.natour21.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.auth.result.step.AuthSignInStep;
import com.amplifyframework.auth.result.step.AuthSignUpStep;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.rx.RxAmplify;
import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.R;
import com.example.natour21.entities.Utente;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.sharedprefs.UserSessionManager;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ControllerLogin extends AppCompatActivity{
    private EditText email, password;
    private TextView registrazione, pass_dim, txt_reg, accesso_admin;
    private Button login, fb, google, eye;
    private Animation anim_btn = null, anim_txtview = null;
    private int counter = 0, r;

    private DAOUtente DAOUtente;
    private String confirmingCodeUtenteId;

    private enum FederatedSignInResult{
        NOT_FOUND_IN_BACKEND,
        FOUND,
        CANT_SAVE_SESSION_LOCALLY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_login);

        try{
            DAOUtente = DAOFactory.getDAOUtente();
        }
        catch (InvalidConnectionSettingsException icse){
            showUserFriendlyErrorMessageAndLogThrowable("Errore. Riavviare l'applicazione.", icse);
        }

        email = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        registrazione = findViewById(R.id.textViewSignUp);
        pass_dim = findViewById(R.id.textViewPasswordRecovery);
        login = findViewById(R.id.buttonLogin);
        fb = findViewById(R.id.btnFbLogin);
        google = findViewById(R.id.btnGoogleLogin);
        eye = findViewById(R.id.eye);
        txt_reg = findViewById(R.id.textViewRegister);
        accesso_admin = findViewById(R.id.accesso_admin);

        String userId = UserSessionManager.getInstance().getUserId();
        if (userId != null) email.setText(userId);

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
        login.setOnClickListener(v -> onLoginClick());
        fb.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Il login con Facebook verrà implementato successivamente.", Toast.LENGTH_SHORT)
                .show());

        google.setOnClickListener(v -> RxAmplify.Auth.signInWithSocialWebUI(AuthProvider.google(), this)
        .subscribe(
                result -> {
                    if (result.isSignInComplete()){
                        onSignInComplete(RxAmplify.Auth.getCurrentUser().getUserId());
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Impossibile completare il login.", Toast.LENGTH_LONG).show();
                        Log.e("LOGIN", "Step mancante nel login: " + result.getNextStep());
                    }
                },
                error -> showUserFriendlyErrorMessageAndLogThrowable("Errore nell'accesso.", error)
        ));
        accesso_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(ControllerLogin.this, Controller_AccessoAdmin.class));
            }
        });

        accesso_admin.setVisibility(View.INVISIBLE);

    }

    private void onSignInComplete(String email)
    {
        UserSessionManager userSessionManager = UserSessionManager.getInstance();
        userSessionManager.setKeys(email);
        userSessionManager.setLoggedIn(true);
        Log.i("AuthQuickstart","Sign in succeeded");
        Callable<FederatedSignInResult> doesUtenteExistInBackendCallable = () ->
                (DAOUtente.getUtenteByEmail(email) != null)
                        ? FederatedSignInResult.FOUND
                        : FederatedSignInResult.NOT_FOUND_IN_BACKEND;
        Callable<FederatedSignInResult> userSessionManagerSaveUserCallable = () ->
                (userSessionManager.saveUserSessionBlocking())
                        ? FederatedSignInResult.FOUND
                        : FederatedSignInResult.CANT_SAVE_SESSION_LOCALLY;
        Observable.fromCallable(doesUtenteExistInBackendCallable)
                .flatMap(firstResult -> {
                    if (firstResult == FederatedSignInResult.FOUND){
                        return Observable.fromCallable(userSessionManagerSaveUserCallable);
                    } else return Observable.just(FederatedSignInResult.NOT_FOUND_IN_BACKEND);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(finalResult -> {
                    if (finalResult == FederatedSignInResult.FOUND
                            || finalResult == FederatedSignInResult.CANT_SAVE_SESSION_LOCALLY){
                        login.startAnimation(anim_btn);
                        startActivity(new Intent(ControllerLogin.this, Controller_Home.class));
                    } else if (finalResult == FederatedSignInResult.NOT_FOUND_IN_BACKEND){
                        Intent newFederatedUserIntent = new Intent(ControllerLogin.this, ControllerRegistrazione.class);
                        newFederatedUserIntent.putExtra("FEDERATED_ACCOUNT_EMAIL", email);
                        startActivity(newFederatedUserIntent);
                    }
                }, error -> showUserFriendlyErrorMessageAndLogThrowable("Impossibile salvare le informazioni d'accesso", error));
    }

    private void onLoginClick(){
        String userEmail =  email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        RxAmplify.Auth.signIn(userEmail, userPassword)
                .subscribe(result -> onSignInResult(result, userEmail),
                        error -> showUserFriendlyErrorMessageAndLogThrowable("Errore nell'accesso.", error)
                );
    }

    private void showUserFriendlyErrorMessageAndLogThrowable(String s, Throwable throwable){
        if (throwable != null) Log.e("LOGIN", throwable.getMessage(), throwable);
        else Log.e("LOGIN", s);
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void onSignInResult(AuthSignInResult result, String utenteId){
        if (result.isSignInComplete()){
            Callable<Boolean> doesUtenteExistInBackendCallable = () -> DAOUtente.getUtenteByEmail(utenteId) != null;
            Observable.fromCallable(doesUtenteExistInBackendCallable)
                    .flatMap(isUtenteInserted -> {
                        if (!isUtenteInserted){
                            RxAmplify.Auth.deleteUser()
                                    .subscribe(()-> {
                                        Toast.makeText(getApplicationContext(), "Impossibile completare la registrazione. "
                                         + "Eseguirla di nuovo.", Toast.LENGTH_LONG).show();
                                    }, error ->
                                            showUserFriendlyErrorMessageAndLogThrowable("Errore. Contattare l'assistenza.", error));
                            return Observable.just(false);
                        }
                        UserSessionManager.getInstance().setKeys(utenteId);
                        return Observable.just(UserSessionManager.getInstance().saveUserSessionBlocking());
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(finalResult -> {
                                if (finalResult) startActivity(new Intent(ControllerLogin.this, Controller_Home.class));
                            },
                            error -> showUserFriendlyErrorMessageAndLogThrowable("Errore nella registrazione.", error));

        }
        else if (result.getNextStep().getSignInStep().equals(AuthSignInStep.CONFIRM_SIGN_UP)){
            RxAmplify.Auth.deleteUser()
                    .subscribe(()-> {
                        Toast.makeText(getApplicationContext(), "Registrazione incompleta."
                                + "Eseguirla di nuovo.", Toast.LENGTH_LONG).show();
                    }, error ->
                            showUserFriendlyErrorMessageAndLogThrowable("Errore. Contattare l'assistenza.", error));
        }
    }



    private void onSignInError(Throwable error){
        if (error instanceof AuthException.UserNotFoundException)
            showUserFriendlyErrorMessageAndLogThrowable("Utente non trovato.", error);
        if (error instanceof AuthException.InvalidPasswordException)
            showUserFriendlyErrorMessageAndLogThrowable("Password errata.", error);
        if (error instanceof AuthException.UserNotConfirmedException){
            RxAmplify.Auth.deleteUser()
                    .subscribe(()-> {
                        Toast.makeText(getApplicationContext(), "Registrazione incompleta."
                                + "Eseguirla di nuovo.", Toast.LENGTH_LONG).show();
                    }, deleteUserError ->
                            showUserFriendlyErrorMessageAndLogThrowable("Errore. Contattare l'assistenza.", deleteUserError));
        }
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
