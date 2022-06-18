package com.example.natour21.controllers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOCorrezioneItinerario;
import com.example.natour21.DAOs.DAOItinerario;
import com.example.natour21.DAOs.DAOSegnalazione;
import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.R;
import com.example.natour21.controllers.dialogs.DialogFragmentControllerAddNewCorrezioneItinerario;
import com.example.natour21.controllers.dialogs.DialogFragmentControllerAddNewSegnalazione;
import com.example.natour21.entities.CorrezioneItinerario;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.entities.Segnalazione;
import com.example.natour21.entities.Utente;
import com.example.natour21.enums.DifficoltaItinerario;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.sharedprefs.UserSessionManager;
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ControllerItinerarioDetails extends AppCompatActivity {


    private DAOItinerario DAOItinerario;
    private DAOSegnalazione DAOSegnalazione;
    private DAOUtente DAOUtente;
    private DAOCorrezioneItinerario DAOCorrezioneItinerario;

    private TextView textViewNomePercorso, textViewAutore, textViewDifficolta,
            textViewNomePuntoIniziale, textViewDurata, textViewNumeroSegnalazioni, textViewDescrizione,
            textViewAccessibilitaMotoria, textViewAccessibilitaVisiva;

    private LinearLayout layoutVisualAccessibility, layoutMobilityAccessibility;

    private Button btnSegnalazione, btnCorrezioneItinerario;
    private ImageButton btnBack;

    private Itinerario itinerario;
    private List<Segnalazione> segnalazioni;
    private List<CorrezioneItinerario> correzioni;
    private Utente utenteAuthor;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerario);
        try {
            DAOItinerario = DAOFactory.getDAOItinerario();
            DAOSegnalazione = DAOFactory.getDAOSegnalazione();
            DAOUtente = DAOFactory.getDAOUtente();
            DAOCorrezioneItinerario = DAOFactory.getDAOCorrezioneItinerario();
        } catch (InvalidConnectionSettingsException icse) {
            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable(getApplicationContext(), "ItinerarioDetails",
                    "Impossibile visualizzare l'itinerario.", icse);
        }

        Bundle bundle = getIntent().getExtras();
        if (!bundle.containsKey("ITINERARIO_ID")) {
            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                    (getApplicationContext(), "ItinerarioDetails", "Impossibile visualizzare l'itinerario.", null);
            finish();
        }

        long idItinerario = bundle.getLong("ITINERARIO_ID");

        textViewNomePercorso = findViewById(R.id.textViewNomeItinerario);
        textViewAutore = findViewById(R.id.textViewAutore);
        textViewDifficolta = findViewById(R.id.textViewDifficoltaItinerario);
        textViewNomePuntoIniziale = findViewById(R.id.textViewNomePuntoIniziale);
        textViewDurata = findViewById(R.id.textViewDurata);
        textViewNumeroSegnalazioni = findViewById(R.id.textViewNumeroSegnalazioni);
        textViewDescrizione = findViewById(R.id.textViewDescrizione);
        textViewAccessibilitaMotoria = findViewById(R.id.textViewAccessibilitaMotoria);
        textViewAccessibilitaVisiva = findViewById(R.id.textViewAccessibilitaVisiva);
        layoutMobilityAccessibility = findViewById(R.id.layoutMobility);
        layoutVisualAccessibility = findViewById(R.id.layoutVisual);
        btnSegnalazione = findViewById(R.id.btnSegnalazione);
        btnCorrezioneItinerario = findViewById(R.id.btnCorreggi);
        btnBack = findViewById(R.id.btnBack);


        Callable<Void> queryCallable = () -> {
            itinerario = DAOItinerario.getItinerarioById(idItinerario);
            segnalazioni = DAOSegnalazione.getSegnalazioneByItinerario(itinerario);
            correzioni = DAOCorrezioneItinerario.getCorrezioneItinerarioByItinerario(itinerario);
            utenteAuthor = DAOUtente.getUtenteByEmail(itinerario.getAuthorId());
            return null;
        };

        Completable.fromCallable(queryCallable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() ->
                        {
                            int valutazioniDifficolta = 1;
                            int valutazioniDurata = 1;
                            int difficoltaScore = itinerario.getDifficoltaItinerario().ordinal();
                            int durataScore = itinerario.getDurata();
                            DifficoltaItinerario difficolta;
                            Integer durata;

                            for (CorrezioneItinerario c : correzioni) {
                                difficolta = c.getDifficolta();
                                durata = c.getDurata();

                                if (difficolta != null) {
                                    valutazioniDifficolta++;
                                    difficoltaScore += difficolta.ordinal();
                                }
                                if (durata != null) {
                                    valutazioniDurata++;
                                    durataScore += durata;
                                }
                                difficoltaScore /= valutazioniDifficolta;
                                durataScore /= valutazioniDurata;

                            }
                            itinerario.setDifficoltaItinerario
                                    (DifficoltaItinerario.values()[difficoltaScore]);
                            itinerario.setDurata(durataScore);
                            textViewNomePercorso.setText(itinerario.getNome());
                            textViewAutore.setText(utenteAuthor.getDisplayName());
                            textViewAutore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent profileIntent = new Intent
                                            (ControllerItinerarioDetails.this, ControllerProfile.class);
                                    profileIntent.putExtra("USER_ID", utenteAuthor.getEmail());
                                    startActivity(profileIntent);
                                }
                            });
                            textViewDifficolta.setText(itinerario.getDifficoltaItinerario().toString()
                                    + (valutazioniDifficolta > 1 ? " (" + valutazioniDifficolta + ")" : ""));
                            textViewNomePuntoIniziale.setText(itinerario.getNomePuntoIniziale());
                            textViewDurata.setText(itinerario.getDurataAsHourMinuteString()
                                    + (valutazioniDurata > 1 ? " (" + valutazioniDurata + ")" : ""));
                            textViewNumeroSegnalazioni.setText(Integer.valueOf(segnalazioni.size()).toString());
                            textViewDescrizione.setText(itinerario.getDescrizione());
                            if (itinerario.getAccessibleMobilityImpairment() != null) {
                                textViewAccessibilitaMotoria.setText
                                        (itinerario.getAccessibleMobilityImpairment()
                                                ? "Sì"
                                                : "No");
                                layoutMobilityAccessibility.setVisibility(View.VISIBLE);

                            }
                            if (itinerario.getAccessibleVisualImpairment() != null) {
                                textViewAccessibilitaVisiva.setText
                                        (itinerario.getAccessibleVisualImpairment()
                                                ? "Sì"
                                                : "No");
                                layoutVisualAccessibility.setVisibility(View.VISIBLE);
                            }

                            if (itinerario.getAuthorId().equals(UserSessionManager.getInstance().getUserId())) {
                                btnCorrezioneItinerario.setVisibility(View.INVISIBLE);
                                btnSegnalazione.setVisibility(View.INVISIBLE);
                            }
                        }
                        , error -> ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                                (getApplicationContext(), "ItinerarioDetails", "Errore nella visualizzazione dell'itinerario.",
                                        error));


        btnSegnalazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragmentControllerAddNewSegnalazione.OnPositiveButtonClickListener onPositiveButtonClickListener =
                        new DialogFragmentControllerAddNewSegnalazione.OnPositiveButtonClickListener() {
                            @Override
                            public void onPositiveButtonClick(boolean areFieldsProperlyFilled, String segnalazioneTitolo, String segnalazioneDescrizione) {
                                if (!areFieldsProperlyFilled) {
                                    Toast.makeText(getApplicationContext(),
                                            "Campi non riempiti correttamente. Riprova.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Segnalazione segnalazione = new Segnalazione(UserSessionManager.getInstance().getUserId(), idItinerario,
                                        segnalazioneTitolo, segnalazioneDescrizione);
                                Completable.fromCallable(() -> DAOSegnalazione.insertSegnalazione(segnalazione))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> Toast.makeText(getApplicationContext(),
                                                "Segnalazione inserita!", Toast.LENGTH_SHORT).show(), error -> {
                                            Toast.makeText(getApplicationContext(),
                                                    "Impossibile inserire la segnalazione. Forse ne hai già fatta una?", Toast.LENGTH_SHORT).show();
                                            Log.e("Segnalazione", error.getMessage(), error);
                                        });
                            }
            };
                DialogFragment segnalazioneDialogFragment = new DialogFragmentControllerAddNewSegnalazione
                        (onPositiveButtonClickListener);
                segnalazioneDialogFragment.show(getSupportFragmentManager(), "segnalazione");
            }
        });

        btnCorrezioneItinerario.setOnClickListener(v -> {
            DialogFragment correzioneDialogFragment;
            DialogFragmentControllerAddNewCorrezioneItinerario.OnPositiveButtonClickListener
                    onPositiveButtonClickListener =
                    new DialogFragmentControllerAddNewCorrezioneItinerario.OnPositiveButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick(boolean areFieldsProperlyFilled, Integer durata,
                                                          DifficoltaItinerario difficoltaItinerario) {
                            if (!areFieldsProperlyFilled) {
                                Toast.makeText(getApplicationContext(),
                                        "Campi non riempiti correttamente. Riprova.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            CorrezioneItinerario correzioneItinerario =
                                    new CorrezioneItinerario(UserSessionManager.getInstance().getUserId(),
                                            idItinerario, difficoltaItinerario, durata);
                            Completable.fromCallable(() -> DAOCorrezioneItinerario.insertCorrezioneItinerario(correzioneItinerario))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> Toast.makeText(getApplicationContext(),
                                            "Correzione inserita!", Toast.LENGTH_SHORT).show(),
                                            error -> {
                                        Toast.makeText(getApplicationContext(),
                                                "Impossibile inserire la correzione. Forse ne hai già fatta una?", Toast.LENGTH_SHORT).show();
                                        Log.e("Correzione", error.getMessage(), error);
                                    });
                        }
                    };
            correzioneDialogFragment = new DialogFragmentControllerAddNewCorrezioneItinerario(onPositiveButtonClickListener);
            correzioneDialogFragment.show(getSupportFragmentManager(), "correzioneitinerario");
        });
        btnBack.setOnClickListener(v -> onBackPressed());
    }


}
