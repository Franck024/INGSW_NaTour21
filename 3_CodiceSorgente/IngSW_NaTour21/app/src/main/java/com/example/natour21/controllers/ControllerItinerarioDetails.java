package com.example.natour21.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.natour21.entities.CorrezioneItinerario;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.entities.Segnalazione;
import com.example.natour21.entities.Utente;
import com.example.natour21.enums.DifficoltaItinerario;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.sharedprefs.UserSessionManager;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Controller_itinerario extends AppCompatActivity {


    private DAOItinerario DAOItinerario;
    private DAOSegnalazione DAOSegnalazione;
    private DAOUtente DAOUtente;
    private DAOCorrezioneItinerario DAOCorrezioneItinerario;

    private TextView textViewNomePercorso, textViewNomeUtente, textViewDifficolta,
            textViewInizio, textViewDurata, textViewWarning, textViewDescrizione,
            textViewAccessibilitaMotoria, textViewAccessibilitaVisiva;

    private LinearLayout layoutVisualAccessibility, layoutMobilityAccessibility;

    private Button btnSegnalazione;

    private Itinerario itinerario;
    private List<Segnalazione> segnalazioni;
    private List<CorrezioneItinerario> correzioni;
    private Utente utenteAuthor;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_itinerario);
        try {
            DAOItinerario = DAOFactory.getDAOItinerario();
            DAOSegnalazione = DAOFactory.getDAOSegnalazione();
            DAOUtente = DAOFactory.getDAOUtente();
            DAOCorrezioneItinerario = DAOFactory.getDAOCorrezioneItinerario();
        } catch (InvalidConnectionSettingsException icse) {
            //
        }

        Bundle bundle = getIntent().getExtras();
        if (!bundle.containsKey("ITINERARIO_ID")) {
            showAndLogErrorMessage("ERRORE: NESSUN ID ITINERARIO.");
            finish();
        }

        long idItinerario = bundle.getLong("ITINERARIO_ID");

        textViewNomePercorso = findViewById(R.id.nomePercorsoItin);
        textViewNomeUtente = findViewById(R.id.nomeUtenteItin);
        textViewDifficolta = findViewById(R.id.diffItin);
        textViewInizio = findViewById(R.id.puntoInizialeItin);
        textViewDurata = findViewById(R.id.durataItin);
        textViewWarning = findViewById(R.id.warning);
        textViewDescrizione = findViewById(R.id.descrizioneItin);
        textViewAccessibilitaMotoria = findViewById(R.id.textViewAccessibilitaMotoria);
        textViewAccessibilitaVisiva = findViewById(R.id.textViewAccessibilitaVisiva);
        layoutMobilityAccessibility = findViewById(R.id.layoutMobility);
        layoutVisualAccessibility = findViewById(R.id.layoutVisual);
        btnSegnalazione = findViewById(R.id.segnala);


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

                            for (CorrezioneItinerario c : correzioni){
                                difficolta = c.getDifficolta();
                                durata = c.getDurata();

                                if (difficolta != null){
                                    valutazioniDifficolta++;
                                    difficoltaScore += difficolta.ordinal();
                                }
                                if (durata != null){
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
                            textViewNomeUtente.setText(utenteAuthor.getDisplayName());
                            textViewNomeUtente.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent profileIntent = new Intent
                                            (Controller_itinerario.this, Controller_Utente.class);
                                    profileIntent.putExtra("USER_ID", utenteAuthor.getEmail());
                                    startActivity(profileIntent);
                                }
                            });
                            textViewDifficolta.setText(itinerario.getDifficoltaItinerario().toString()
                            + (valutazioniDifficolta > 1 ? " (" + valutazioniDifficolta + ")" : ""));
                            textViewInizio.setText(itinerario.getNomePuntoIniziale());
                            textViewDurata.setText(itinerario.getDurataAsHourMinuteString()
                            + (valutazioniDurata > 1 ? " (" + valutazioniDurata + ")" : ""));
                            textViewWarning.setText(Integer.valueOf(segnalazioni.size()).toString());
                            textViewDescrizione.setText(itinerario.getDescrizione());
                            if (itinerario.getAccessibleMobilityImpairment() != null){
                                textViewAccessibilitaMotoria.setText
                                        (itinerario.getAccessibleMobilityImpairment()
                                                ? "Sì"
                                                : "No");
                                layoutMobilityAccessibility.setVisibility(View.VISIBLE);

                            }
                            if (itinerario.getAccessibleVisualImpairment() != null){
                                textViewAccessibilitaVisiva.setText
                                        (itinerario.getAccessibleVisualImpairment()
                                                ? "Sì"
                                                : "No");
                                layoutVisualAccessibility.setVisibility(View.VISIBLE);
                            }
                        }
                        , error -> showAndLogErrorMessage(error.getMessage()));


        btnSegnalazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment segnalazioneDialogFragment = new SegnalazioneDialogFragment
                        (itinerario.getId(),
                                UserSessionManager.getInstance().getUserId());
                segnalazioneDialogFragment.show(getSupportFragmentManager(), "segnalazione");
            }
        });

    }

    private void showAndLogErrorMessage(String s){
        Log.e("ITINERARIO", s);
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
