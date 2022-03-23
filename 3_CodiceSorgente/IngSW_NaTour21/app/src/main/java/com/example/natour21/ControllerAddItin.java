package com.example.natour21;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerAddItin extends AppCompatActivity {
    private Button annulla, prossimo;
    private EditText nome_precorso, ore, minuti, punto_inizio, descrizione;
    RadioGroup difficolta;
    RadioButton scelta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_aggiungi_itinerario);

        prossimo =findViewById(R.id.btnProssimo);
        annulla = findViewById(R.id.btnIndietro);
        nome_precorso = findViewById(R.id.editTextNome);
        ore = findViewById(R.id.editViewDurataOre);
        minuti = findViewById(R.id.editViewDurataMinuti);
        punto_inizio = findViewById(R.id.editViewPuntoIniziale);
        descrizione = findViewById(R.id.editTextDescrizione);
        difficolta = (RadioGroup) findViewById(R.id.RB_diff);


        prossimo.setOnClickListener(v -> {
            //Controllo campi vuoti
            boolean check = valoriItinerario();
           // prossimo.startAnimation(anim_btn);
            if(check)
                startActivity(new Intent(ControllerAddItin.this, ControllerAddItin_2.class));
        });

        annulla.setOnClickListener(v -> {
            // prossimo.startAnimation(anim_btn);
            startActivity(new Intent(ControllerAddItin.this, Controller_Home.class));
        });

    }

    private boolean valoriItinerario() {

        String S_nome_percorso = nome_precorso.getText().toString().trim();
        String S_ore = ore.getText().toString().trim();
        String S_minuti = minuti.getText().toString().trim();
        String S_punto_inizio = punto_inizio.getText().toString().trim();
        String S_descrizione = descrizione.getText().toString().trim();

        //Scelta radiobutton
        // get selected radio button from radioGroup
        int selectedId = difficolta.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        scelta = (RadioButton) findViewById(selectedId);

        String S_diff = (String) scelta.getText();
        /// fine

        if (S_nome_percorso.isEmpty()) {
            nome_precorso.setError("Campo obbligatorio.");
            nome_precorso.requestFocus();
            return false;
        }

        if (S_ore.isEmpty()) {
            ore.setError("Campo obbligatorio.");
            ore.requestFocus();
            return false;
        }

        if (S_minuti.isEmpty()) {
            minuti.setError("Campo obbligatorio.");
            minuti.requestFocus();
            return false;
        }

        if (S_punto_inizio.isEmpty()) {
            punto_inizio.setError("Campo obbligatorio.");
            punto_inizio.requestFocus();
            return false;
        }

        if(verificaNumerica(S_minuti)==false){
            minuti.setError("Inserisci un numero");
            minuti.requestFocus();
            return false;
        }

        if(verificaNumerica(S_ore)==false){
            ore.setError("Inserisci un numero");
            ore.requestFocus();
            return false;
        }
        return true;
        // *** INVIARE I DATI E SALVARLI  *** //

    }
    private boolean verificaNumerica(String text) {
        return text.matches("[0-9]+") && text.length() < 3;
    }



}
