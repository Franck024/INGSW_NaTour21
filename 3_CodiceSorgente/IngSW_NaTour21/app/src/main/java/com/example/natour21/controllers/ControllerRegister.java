package com.example.natour21.controllers;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.auth.result.step.AuthSignUpStep;
import com.amplifyframework.rx.RxAmplify;
import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.R;
import com.example.natour21.controllers.dialogs.DialogFragmentControllerConfirmationCode;
import com.example.natour21.entities.Utente;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.sharedprefs.UserSessionManager;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ControllerRegister extends AppCompatActivity implements DialogFragmentControllerConfirmationCode.OnSuccessfulCodeConfirmationDismissListener {
    private EditText editTextNome, editTextCognome, editTextEmail, editTextPassword,
            editTextConfirmPassword, editTextCitta, editTextPhoneNumber;
    private CheckBox checkBoxGDPR;
    private Button btnTogglePasswordVisibility, btnToggleConfirmPasswordVisibility, btnRegister;
    private TextView textViewPasswordPolicy, textViewCreateAccount;

    boolean shouldHidePassword = false, shouldHideConfirmPassword = false;
    private Animation animationBtn = null, animationTextView = null;

    private DAOUtente DAOUtente;
    private Utente utente;
    private String federatedAccountEmail;

    private DialogFragmentControllerConfirmationCode dialogFragmentConfirmationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);

        try{
            DAOUtente = DAOFactory.getDAOUtente();
        }
        catch (InvalidConnectionSettingsException icse){
            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                    (getApplicationContext(),"Register", "Errore. Riavviare l'applicazione.", icse);
        }


        editTextNome = findViewById(R.id.editTextNome);
        editTextCognome = findViewById(R.id.editTextCognome);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextCitta = findViewById(R.id.editTextCity);
        editTextPhoneNumber = findViewById(R.id.editTextPhone);

        textViewPasswordPolicy = findViewById(R.id.textViewPasswordRules);
        textViewCreateAccount = findViewById(R.id.textViewCreateAccount);

        btnRegister = findViewById(R.id.btnRegister);
        btnTogglePasswordVisibility = findViewById(R.id.visPass);
        btnToggleConfirmPasswordVisibility = findViewById(R.id.visConPass);

        checkBoxGDPR = findViewById(R.id.checkBoxAcceptTerms);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.containsKey("FEDERATED_ACCOUNT_EMAIL")){
            federatedAccountEmail = bundle.getString("FEDERATED_ACCOUNT_EMAIL");
            if (federatedAccountEmail.isEmpty()) return;
            editTextEmail.setVisibility(View.GONE);
            editTextPassword.setVisibility(View.GONE);
            editTextConfirmPassword.setVisibility(View.GONE);
            textViewPasswordPolicy.setVisibility(View.GONE);
            btnTogglePasswordVisibility.setVisibility(View.GONE);
            btnToggleConfirmPasswordVisibility.setVisibility(View.GONE);
        }

        animationBtn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_btn);
        animationTextView = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_textview);

        animazioni();

        //Click
        btnTogglePasswordVisibility.setOnClickListener(v -> {
            shouldHidePassword = !shouldHidePassword;
            if (shouldHidePassword)
            {
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnTogglePasswordVisibility.getBackground().setAlpha(50);
            }
            else{
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnTogglePasswordVisibility.getBackground().setAlpha(200);
            }
        });

        btnToggleConfirmPasswordVisibility.setOnClickListener(view -> {
            shouldHideConfirmPassword = !shouldHideConfirmPassword;
            if (shouldHideConfirmPassword) {
                editTextConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnToggleConfirmPasswordVisibility.getBackground().setAlpha(50);
            } else {
                editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnToggleConfirmPasswordVisibility.getBackground().setAlpha(200);
            }
        });

        btnRegister.setOnClickListener(v -> {
            btnRegister.startAnimation(animationBtn);
            if (federatedAccountEmail == null || federatedAccountEmail.isEmpty()) registerUser();
            else registerFederatedUser();
        });

        editTextNome.setOnClickListener(v -> editTextNome.startAnimation(animationTextView));
        editTextCognome.setOnClickListener(v -> editTextCognome.startAnimation(animationTextView));
        editTextEmail.setOnClickListener(v -> editTextEmail.startAnimation(animationTextView));
        editTextPassword.setOnClickListener(v -> editTextPassword.startAnimation(animationTextView));
        editTextCitta.setOnClickListener(v -> editTextCitta.startAnimation(animationTextView));
        editTextConfirmPassword.setOnClickListener(v -> editTextConfirmPassword.startAnimation(animationTextView));
        editTextPhoneNumber.setOnClickListener(v -> editTextPhoneNumber.startAnimation(animationTextView));
    }




    private void registerUser() {
        String nameText = editTextNome.getText().toString().trim();
        String surnameText = editTextCognome.getText().toString().trim();
        String emailText = editTextEmail.getText().toString().trim();
        String passwordText = editTextPassword.getText().toString().trim();
        String confPasswordText = editTextConfirmPassword.getText().toString().trim();
        String cityText = editTextCitta.getText().toString().trim();
        String telephoneText = editTextPhoneNumber.getText().toString().trim();
        if (cityText.equals("")) cityText = null;
        if (telephoneText.equals("")) telephoneText = null;

        if (nameText.isEmpty()) {
            editTextNome.setError("Campo obbligatorio.");
            editTextNome.requestFocus();
            return;
        }

        if (surnameText.isEmpty()) {
            editTextCognome.setError("Campo obbligatorio.");
            editTextCognome.requestFocus();
            return;
        }

        if (emailText.isEmpty()) {
            editTextEmail.setError("Campo obbligatorio.");
            editTextEmail.requestFocus();
            return;
        }

        if (passwordText.isEmpty()) {
            editTextPassword.setError("Campo obbligatorio.");
            editTextPassword.requestFocus();
            return;
        }

        if (confPasswordText.isEmpty()) {
            editTextConfirmPassword.setError("Campo obbligatorio.");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (!verificaPass(passwordText)) {
            editTextPassword.setError("La password non rispetta le policy.");
            editTextPassword.requestFocus();
            return;
        }

        if (!(passwordText.equals(confPasswordText))) {
            editTextPassword.setError("Le password non corrispondono.");
            editTextPassword.requestFocus();

            editTextConfirmPassword.setError("Le password non corrispondono.");
            editTextConfirmPassword.requestFocus();

            return;
        }

        if (!(verificaEmail(emailText))) {
            editTextEmail.setError("Email non valida.");
            editTextEmail.requestFocus();
            return;
        }

        if (!(checkBoxGDPR.isChecked())) {
            Toast.makeText(ControllerRegister.this,
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
        String nameText = editTextNome.getText().toString().trim();
        String surnameText = editTextCognome.getText().toString().trim();
        String cityText = editTextCitta.getText().toString().trim();
        String telephoneText = editTextPhoneNumber.getText().toString().trim();
        if (cityText.equals("")) cityText = null;
        if (telephoneText.equals("")) telephoneText = null;

        if (nameText.isEmpty()) {
            editTextNome.setError("Campo obbligatorio.");
            editTextNome.requestFocus();
            return;
        }

        if (surnameText.isEmpty()) {
            editTextCognome.setError("Campo obbligatorio.");
            editTextCognome.requestFocus();
            return;
        }

        if (!(checkBoxGDPR.isChecked())) {
            Toast.makeText(ControllerRegister.this,
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
                    startActivity(new Intent(ControllerRegister.this, ControllerHome.class ));
                }, error -> ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                        (getApplicationContext(),"Register", "Errore nella registrazione.", error));


    }

    private void onSignUpResult(AuthSignUpResult result, Utente utente){
        String email = utente.getEmail();
        if (result.getNextStep().getSignUpStep().equals(AuthSignUpStep.CONFIRM_SIGN_UP_STEP)){
            this.utente = utente;
            dialogFragmentConfirmationCode =
                    new DialogFragmentControllerConfirmationCode(this);
            dialogFragmentConfirmationCode.show(getSupportFragmentManager(), "confirmationcode");
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
                                startActivity(new Intent(ControllerRegister.this, ControllerHome.class ));
                            }, error -> ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                            (getApplicationContext(),"Register", "Errore nella registrazione.", error));
        }

    }

    private void onSignUpError(Throwable error){
        if ((error instanceof AuthException.UsernameExistsException)
                || (error instanceof AuthException.AliasExistsException))
            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                    (getApplicationContext(),"Register", "Un utente con la stessa email esiste già.", error);
        else ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                (getApplicationContext(),"Register", "Impossibile eseguire la registrazione.", error);
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
        textViewCreateAccount.setTranslationY(1000);
        textViewCreateAccount.animate().translationY(0).setDuration(700).setStartDelay(200).start();
        editTextNome.setTranslationX(1000);
        editTextNome.animate().translationX(0).setDuration(700).setStartDelay(600).start();
        editTextCognome.setTranslationX(1000);
        editTextCognome.animate().translationX(0).setDuration(700).setStartDelay(800).start();
        editTextEmail.setTranslationX(1000);
        editTextEmail.animate().translationX(0).setDuration(700).setStartDelay(1000).start();
        editTextCitta.setTranslationX(1000);
        editTextCitta.animate().translationX(0).setDuration(700).setStartDelay(1400).start();
        editTextPhoneNumber.setTranslationX(1000);
        editTextPhoneNumber.animate().translationX(0).setDuration(700).setStartDelay(1200).start();
        textViewPasswordPolicy.setTranslationX(1000);
        textViewPasswordPolicy.animate().translationX(0).setDuration(700).setStartDelay(1600).start();
        editTextPassword.setTranslationX(1000);
        editTextPassword.animate().translationX(0).setDuration(700).setStartDelay(1800).start();
        editTextConfirmPassword.setTranslationX(1000);
        editTextConfirmPassword.animate().translationX(0).setDuration(700).setStartDelay(2000).start();
        btnTogglePasswordVisibility.setTranslationX(1000);
        btnTogglePasswordVisibility.animate().translationX(0).setDuration(700).setStartDelay(1800).start();
        btnToggleConfirmPasswordVisibility.setTranslationX(1000);
        btnToggleConfirmPasswordVisibility.animate().translationX(0).setDuration(700).setStartDelay(2000).start();
        checkBoxGDPR.setTranslationY(1000);
        checkBoxGDPR.animate().translationY(0).setDuration(700).setStartDelay(1400).start();
        btnRegister.setTranslationY(1000);
        btnRegister.animate().translationY(0).setDuration(800).setStartDelay(1800).start();
    }

    @Override
    public void onSuccessfulCodeConfirmationDismiss(String code){
        AlertDialog alertDialog = (AlertDialog) dialogFragmentConfirmationCode.getDialog();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        RxAmplify.Auth.confirmSignUp(utente.getEmail(), code)
                .flatMap(result -> {
                    if (!result.isSignUpComplete()) throw new Exception("Invalid signup state.");
                    onSignUpResult(result, utente);
                    dialogFragmentConfirmationCode.dismiss();
                    return Single.just(true);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unusedResult -> {}, error -> onConfirmationCodeError(error));

    }

    private void onConfirmationCodeError(Throwable error){
        if (dialogFragmentConfirmationCode == null) {
            Log.e("Register", "DialogFragment per il codice di conferma è inaspettatamente null!");
            return;
        }
        AlertDialog alertDialog = (AlertDialog) dialogFragmentConfirmationCode.getDialog();
        if (error instanceof AuthException.CodeMismatchException){
            dialogFragmentConfirmationCode.setTextViewErrorTextAndShow("Codice errato.");
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
        } else if (error instanceof AuthException.CodeExpiredException){
            Completable
                    .fromCallable(()-> RxAmplify.Auth.resendSignUpCode(utente.getEmail()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                                dialogFragmentConfirmationCode.setTextViewErrorTextAndShow("Il codice inserito è scaduto. Un altro codice è stato inviato alla tua email.");
                                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
                                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                            }
                            , codeResendError -> {
                                dialogFragmentConfirmationCode.setTextViewErrorTextAndShow("Il codice inserito è scaduto, ma è impossibile mandare un altro codice. Riprova più tardi.");
                                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
                                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                                Log.e("ConfirmationCodeDialog", codeResendError.getMessage(), codeResendError);
                            });
        }
    }
}

