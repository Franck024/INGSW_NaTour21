package com.example.natour21.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.natour21.PopupSegnalazione;
import com.example.natour21.R;
import com.example.natour21.entities.Itinerario;

public class Controller_itinerario extends AppCompatActivity {
    TextView nomePercorso, nomeUtente, difficolta, inizio, fine, tempo, warning, descrizione;
    Button segnalazione;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_itinerario);

        nomePercorso = findViewById(R.id.nomePercorsoItin);
        nomeUtente = findViewById(R.id.nomeUtenteItin);
        difficolta = findViewById(R.id.diffItin);
        inizio = findViewById(R.id.inizioItin);
        fine = findViewById(R.id.fineItin);
        tempo = findViewById(R.id.tempoItin);
        warning = findViewById(R.id.warning);
        descrizione = findViewById(R.id.descrizioneItin);
        segnalazione = findViewById(R.id.segnala);

/* sono vuoti
        nomePercorso.setText(Itinerario.getNome());
        nomeUtente.setText(Itinerario.getAuthorId());
        difficolta.setText(Itinerario.getDifficoltaItinerario().toString());
        inizio.setText(Itinerario.getNomePuntoIniziale());
       // fine.setText(Itinerario.getAuthorId());
        tempo.setText(Itinerario.getDurata());
        warning.setText(Itinerario.getWarning());
        descrizione.setText(Itinerario.getDescrizione);
 */
        segnalazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int1 = new Intent(getApplicationContext(), PopupSegnalazione.class);
                startActivity(int1);
            }
        });


    }
}
