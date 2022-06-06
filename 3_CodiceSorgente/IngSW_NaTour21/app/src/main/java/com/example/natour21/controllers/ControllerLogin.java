package com.example.natour21.controllers;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.step.AuthSignInStep;
import com.amplifyframework.rx.RxAmplify;
import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOStatistiche;
import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.R;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.sharedprefs.UserSessionManager;
import com.google.android.gms.common.util.ArrayUtils;

import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ControllerLogin extends AppCompatActivity{
    private EditText editTextEmail, editTextPassword;
    private TextView textViewClickToRegister, textViewRetrievePassword, textViewRegister, accesso_admin;
    private Button btnLogin, btnSignInWithFacebook, btnSignInWithGoogle, btnHidePassword;
    private Animation animationBtn = null, animationTextView = null;

    private boolean shouldHidePassword = false;

    private DAOUtente DAOUtente;
    private DAOStatistiche DAOStatistiche;

    private enum FederatedSignInResult{
        NOT_FOUND_IN_BACKEND,
        FOUND,
        CANT_SAVE_SESSION_LOCALLY
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try{
            DAOUtente = DAOFactory.getDAOUtente();
            DAOStatistiche = DAOFactory.getDAOStatistiche();
        }
        catch (InvalidConnectionSettingsException icse){
            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                    (getApplicationContext(),"Login", "Errore. Riavviare l'applicazione.", icse);
        }

        editTextEmail = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewClickToRegister = findViewById(R.id.textViewClickToRegister);
        textViewRetrievePassword = findViewById(R.id.textViewPasswordRecovery);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignInWithFacebook = findViewById(R.id.btnSignInWithFacebook);
        btnSignInWithGoogle = findViewById(R.id.btnSignInWithGoogle);
        btnHidePassword = findViewById(R.id.btnHidePassword);
        textViewRegister = findViewById(R.id.textViewRegister);

        String userId = UserSessionManager.getInstance().getUserId();
        String[] federatedSignInSubDomains = {"gmail.com"};
        if (userId != null && !ArrayUtils.contains(federatedSignInSubDomains, userId.substring(userId.indexOf('@')+1)))
            editTextEmail.setText(userId);

        animationBtn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_btn);
        animationTextView = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_textview);

        animazioni();

        btnHidePassword.setOnClickListener(v -> {
            shouldHidePassword = !shouldHidePassword;
            editTextPassword.setTransformationMethod(shouldHidePassword
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            btnHidePassword.getBackground().setAlpha(shouldHidePassword
                    ? 50
                    : 200);
        });

        editTextEmail.setOnClickListener(v -> editTextEmail.startAnimation(animationTextView));
        editTextPassword.setOnClickListener(v -> editTextPassword.startAnimation(animationTextView));

        textViewClickToRegister.setOnClickListener(view -> startActivity(new Intent(ControllerLogin.this, ControllerRegister.class)));
        textViewRetrievePassword.setOnClickListener(view -> startActivity(new Intent(ControllerLogin.this, ControllerRecovery.class)));

        btnLogin.setOnClickListener(v -> onLoginClick());
        btnSignInWithFacebook.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Il login con Facebook verrà implementato successivamente.", Toast.LENGTH_SHORT)
                .show());

        btnSignInWithGoogle.setOnClickListener(v -> RxAmplify.Auth.signInWithSocialWebUI(AuthProvider.google(), this)
        .subscribe(
                result -> onFederatedSignInResult(result, RxAmplify.Auth.getCurrentUser().getUserId()),
                error -> runOnUiThread(() -> ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                        (getApplicationContext(),"Login", "Errore nell'accesso.", error))
        ));

    }

    private void onFederatedSignInResult(AuthSignInResult result, String email)
    {
        if (!result.isSignInComplete()){
            Toast.makeText(getApplicationContext(), "Impossibile completare il login.", Toast.LENGTH_LONG).show();
            Log.e("Login", "Step mancante nel login: " + result.getNextStep());
            return;
        }
        UserSessionManager userSessionManager = UserSessionManager.getInstance();
        userSessionManager.setKeys(email);
        userSessionManager.setLoggedIn(true);
        Log.i("AuthQuickstart","Sign in succeeded");
        Callable<FederatedSignInResult> doesUtenteExistInBackendCallable = () ->
                (DAOUtente.getUtenteByEmail(email) != null)
                        ? FederatedSignInResult.FOUND
                        : FederatedSignInResult.NOT_FOUND_IN_BACKEND;
        Callable<FederatedSignInResult> increaseAccessCountStatAndSaveUserSessionCallable = () -> {
            DAOStatistiche.incrementUtenteAccess();
            return (userSessionManager.saveUserSessionBlocking())
                    ? FederatedSignInResult.FOUND
                    : FederatedSignInResult.CANT_SAVE_SESSION_LOCALLY;
        };
        Observable.fromCallable(doesUtenteExistInBackendCallable)
                .flatMap(firstResult -> {
                    if (firstResult == FederatedSignInResult.FOUND){
                        return Observable.fromCallable(increaseAccessCountStatAndSaveUserSessionCallable);
                    } else return Observable.just(FederatedSignInResult.NOT_FOUND_IN_BACKEND);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(finalResult -> {
                    if (finalResult == FederatedSignInResult.FOUND
                            || finalResult == FederatedSignInResult.CANT_SAVE_SESSION_LOCALLY){
                        btnLogin.startAnimation(animationBtn);
                        startActivity(new Intent(ControllerLogin.this, ControllerHome.class));
                    } else if (finalResult == FederatedSignInResult.NOT_FOUND_IN_BACKEND){
                        Intent newFederatedUserIntent = new Intent(ControllerLogin.this, ControllerRegister.class);
                        newFederatedUserIntent.putExtra("FEDERATED_ACCOUNT_EMAIL", email);
                        startActivity(newFederatedUserIntent);
                    }
                }, error -> runOnUiThread(() -> ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                        (getApplicationContext(),"Login", "Impossibile salvare le informazioni d'accesso", error)));
    }

    private void onLoginClick(){
        String userEmail =  editTextEmail.getText().toString().trim();
        String userPassword = editTextPassword.getText().toString().trim();
        RxAmplify.Auth.signIn(userEmail, userPassword)
                .subscribe(result -> onSignInResult(result, userEmail),
                        error -> runOnUiThread(() -> onSignInError(error))
                );
    }

    private void onSignInResult(AuthSignInResult result, String utenteId){
        if (result.isSignInComplete()){
            Callable<Boolean> doesUtenteExistInBackendCallable = () -> DAOUtente.getUtenteByEmail(utenteId) != null;
            Observable.fromCallable(doesUtenteExistInBackendCallable)
                    .flatMap(isUtenteInserted -> {
                        if (!isUtenteInserted){
                            RxAmplify.Auth.deleteUser()
                                    .subscribe(()-> {
                                        runOnUiThread( () -> Toast.makeText(getApplicationContext(), "Impossibile completare la registrazione. "
                                                + "Eseguirla di nuovo.", Toast.LENGTH_LONG).show());
                                    }, error -> runOnUiThread(() -> onSignInError(error)));

                            return Observable.just(false);
                        }
                        UserSessionManager.getInstance().setKeys(utenteId);
                        UserSessionManager.getInstance().setLoggedIn(true);
                        Callable<Boolean> increaseAccessCountStatAndSaveUserSessionCallable = () -> {
                            DAOStatistiche.incrementUtenteAccess();
                            return UserSessionManager.getInstance().saveUserSessionBlocking();
                        };
                        return Observable.fromCallable(increaseAccessCountStatAndSaveUserSessionCallable);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(finalResult -> {
                                if (finalResult) startActivity(new Intent(ControllerLogin.this, ControllerHome.class));
                            },
                            error -> onSignInError(error));

        }
        else if (result.getNextStep().getSignInStep().equals(AuthSignInStep.CONFIRM_SIGN_UP)){
            RxAmplify.Auth.deleteUser()
                    .subscribe(()-> {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Registrazione incompleta."
                                + "Eseguirla di nuovo.", Toast.LENGTH_LONG).show());
                    }, error ->
                            runOnUiThread(() -> onSignInError(error)));
        }
    }



    private void onSignInError(Throwable error){
        if (error instanceof AuthException.UserNotFoundException)
            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                    (getApplicationContext(),"Login", "Utente non trovato.", error);
        if (error instanceof AuthException.InvalidPasswordException
        || error instanceof AuthException.NotAuthorizedException)
            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                    (getApplicationContext(),"Login", "Password errata.", error);
        if (error instanceof AuthException.UserNotConfirmedException){
            RxAmplify.Auth.deleteUser()
                    .subscribe(()-> {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Registrazione incompleta."
                                + "Eseguirla di nuovo.", Toast.LENGTH_LONG).show());
                    }, deleteUserError -> runOnUiThread(() -> ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                            (getApplicationContext(),"Login", "Errore. Contattare l'assistenza.", deleteUserError)));
        }
        ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                (getApplicationContext(),"Login", "Errore. Contattare l'assistenza.", error);
    }
    private void animazioni(){
        editTextEmail.setTranslationX(1000);
        editTextEmail.animate().translationX(0).setDuration(800).setStartDelay(400).start();
        editTextPassword.setTranslationX(1000);
        editTextPassword.animate().translationX(0).setDuration(800).setStartDelay(600).start();
        btnLogin.setTranslationX(1000);
        btnLogin.animate().translationX(0).setDuration(800).setStartDelay(800).start();
        textViewRetrievePassword.setTranslationX(1000);
        textViewRetrievePassword.animate().translationX(0).setDuration(800).setStartDelay(1000).start();
        btnHidePassword.setTranslationX(1000);
        btnHidePassword.animate().translationX(0).setDuration(1000).setStartDelay(600).start();
        btnSignInWithFacebook.setTranslationY(1000);
        btnSignInWithFacebook.animate().translationY(0).setDuration(800).setStartDelay(400).start();
        btnSignInWithGoogle.setTranslationY(1000);
        btnSignInWithGoogle.animate().translationY(0).setDuration(800).setStartDelay(600).start();
        textViewRegister.setTranslationY(1000);
        textViewRegister.animate().translationY(0).setDuration(800).setStartDelay(800).start();
        textViewClickToRegister.setTranslationY(1000);
        textViewClickToRegister.animate().translationY(0).setDuration(800).setStartDelay(1000).start();

    }
}
