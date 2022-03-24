package com.example.natour21;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.natour21.map.MapActivity;

public class ControllerAddItin_2 extends AppCompatActivity {
    Button indietro;
    TextView apriMappa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_aggiungi_itinerario2);

        indietro = findViewById(R.id.btnIndietro2);
        apriMappa = findViewById(R.id.openMap);

        indietro.setOnClickListener(v -> startActivity(new Intent(ControllerAddItin_2.this, ControllerAddItin.class)));
        apriMappa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ControllerAddItin_2.this, MapActivity.class));
            }
        });

    }
}
