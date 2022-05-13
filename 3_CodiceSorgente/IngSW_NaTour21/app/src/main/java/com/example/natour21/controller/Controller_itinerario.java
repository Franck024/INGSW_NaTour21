package com.example.natour21.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOItinerario;
import com.example.natour21.DAOs.DAOSegnalazione;
import com.example.natour21.R;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.entities.Segnalazione;
import com.example.natour21.entities.Utente;
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
    private com.example.natour21.DAOs.DAOUtente DAOUtente;

    private TextView textViewNomePercorso, textViewNomeUtente, textViewDifficolta,
            textViewInizio, textViewDurata, textViewWarning, textViewDescrizione;
    private Button btnSegnalazione;

    private Itinerario itinerario;
    private List<Segnalazione> segnalazioni;
    private Utente utenteAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_itinerario);
        try {
            DAOItinerario = DAOFactory.getDAOItinerario();
            DAOSegnalazione = DAOFactory.getDAOSegnalazione();
            DAOUtente = DAOFactory.getDAOUtente();
        } catch (InvalidConnectionSettingsException icse) {
            //
        }

        Bundle bundle = getIntent().getExtras();
        if (!bundle.containsKey("ITINERARIO_ID")) {
            showErrorMessage("ERRORE: NESSUN ID ITINERARIO.");
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
        btnSegnalazione = findViewById(R.id.segnala);


        Callable<Void> queryCallable = () -> {
            itinerario = DAOItinerario.getItinerarioById(idItinerario);
            segnalazioni = DAOSegnalazione.getSegnalazioneByItinerario(itinerario);
            utenteAuthor = DAOUtente.getUtenteByEmail(itinerario.getAuthorId());
            return null;
        };

        Completable.fromCallable(queryCallable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() ->
                        {
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
                            textViewDifficolta.setText(itinerario.getDifficoltaItinerario().toString());
                            textViewInizio.setText(itinerario.getNomePuntoIniziale());
                            textViewDurata.setText(itinerario.getDurataAsHourMinuteString());
                            textViewWarning.setText(Integer.valueOf(segnalazioni.size()).toString());
                            textViewDescrizione.setText(itinerario.getDescrizione());
                        }
                        , error -> showErrorMessage(error.getMessage()));


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



    private void showErrorMessage(String s){
        Log.e("ITINERARIO", s);
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
