package com.example.natour21;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Controller_AccessoAdmin extends AppCompatActivity {
    EditText passAdmin;
    Button bt_accesso_admin;
    ImageButton btn_indietro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accesso_admin);

        passAdmin = findViewById(R.id.passAdmin);
        bt_accesso_admin = findViewById(R.id.btn_accessoAdmin);
        btn_indietro = findViewById(R.id.back_admin);

        bt_accesso_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passAdmin.getText().toString().trim().equals("admin"))
                    startActivity(new Intent(Controller_AccessoAdmin.this, Controller_Statistiche.class));
                else{
                    passAdmin.setError("Password errata");
                    passAdmin.requestFocus();
                }
            }
        });

        btn_indietro.setOnClickListener(view -> startActivity(new Intent(Controller_AccessoAdmin.this, ControllerLogin.class)));
    }
}
