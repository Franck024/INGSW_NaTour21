package com.example.natour21.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.natour21.R;

public class Controller_Ricerca extends AppCompatActivity {
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ricerca_itinerario);

        back = findViewById(R.id.back_ricerca);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back.animate().rotationY(360).withEndAction(
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                startActivity(new Intent(Controller_Ricerca.this, Controller_Home.class));
                            }
                        });
            }
        });
    }
}
