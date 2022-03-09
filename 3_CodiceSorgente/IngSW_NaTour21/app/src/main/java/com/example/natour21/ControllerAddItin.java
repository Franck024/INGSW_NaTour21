package com.example.natour21;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class ControllerAddItin extends AppCompatActivity {
    private Button annulla, prossimo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_aggiungi_itinerario);

        prossimo =findViewById(R.id.btnProssimo);
        annulla = findViewById(R.id.btnIndietro);

        prossimo.setOnClickListener(v -> {
           // prossimo.startAnimation(anim_btn);
            startActivity(new Intent(ControllerAddItin.this, ControllerAddItin_2.class));
        });

        annulla.setOnClickListener(v -> {
            // prossimo.startAnimation(anim_btn);
            startActivity(new Intent(ControllerAddItin.this, ControllerHome.class));
        });

    }

}
