package com.example.natour21.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.natour21.PermissionUtils;
import com.example.natour21.R;
import com.example.natour21.exceptions.InvalidGeoPointStringFormatException;
import com.example.natour21.exceptions.ProviderDisabledException;
import com.example.natour21.map.MapActivity;
import com.example.natour21.map.MapConverter;

import com.nbsp.materialfilepicker.*;

import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.ticofab.androidgpxparser.parser.GPXParser;

public class ControllerAddItin extends AppCompatActivity {
    private Button annulla, prossimo;
    private EditText nome_precorso, ore, minuti, punto_inizio, descrizione;
    RadioGroup radioGroupDifficolta;
    RadioGroup radioGroupTracciato;
    RadioButton radioBtnChosenDifficolta;
    RadioButton radioBtnChosenTracciato;
    ByteArrayInputStream itinerarioToUpload = null;
    ActivityResultLauncher<Void> startForGPXResult;
    ActivityResultLauncher<String> requestPermissionLauncher;
    ActivityResultLauncher<Intent> startForMapResult;

    private class GPXContract extends ActivityResultContract<Void, Uri>{


        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void unused) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("application/*");
            return intent;
        }

        @Override
        public Uri parseResult(int i, @Nullable Intent intent) {
            if (i != Activity.RESULT_OK) return null;

            Uri uri = intent.getData();
            return uri;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_aggiungi_itinerario);

        prossimo = findViewById(R.id.btnProssimo);
        annulla = findViewById(R.id.btnIndietro);
        nome_precorso = findViewById(R.id.editTextNome);
        ore = findViewById(R.id.editViewDurataOre);
        minuti = findViewById(R.id.editViewDurataMinuti);
        punto_inizio = findViewById(R.id.editViewPuntoIniziale);
        descrizione = findViewById(R.id.editTextDescrizione);
        radioGroupDifficolta = findViewById(R.id.RB_diff);
        radioGroupTracciato = findViewById(R.id.radioBtnGroupTracciato);

        startForMapResult = registerForActivityResult
                (new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() != Activity.RESULT_OK) return;

                        Intent intent = result.getData();
                        if (intent == null) return;

                        Bundle bundle = intent.getExtras();
                        String geoPointListString = bundle.getString("GEO_POINT_LIST");
                        itinerarioToUpload =  new ByteArrayInputStream
                                (geoPointListString.getBytes(MapConverter.getCHARSET()));
                    }
                });

        startForGPXResult = registerForActivityResult(
                new GPXContract(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        System.out.println("Risultato ottenuto.");
                        if (result == null) return;
                        Cursor cursor =
                                getContentResolver().query(result,
                                        null, null, null, null);
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        cursor.moveToFirst();
                        String path = cursor.getString(nameIndex);
                        cursor.close();
                        int indexOfFullStop = path.lastIndexOf('.');
                        if (indexOfFullStop == -1) return;
                        String extension = path.substring(indexOfFullStop+1);
                        Log.i("EXT", extension);
                        if (!extension.equals("gpx")) return;
                        System.out.println("File ottenuto con successo.");
                        try{
                            InputStream fileInputStream = getContentResolver().openInputStream(result);
                            GPXParser gpxParser = new GPXParser();
                            itinerarioToUpload = MapConverter.gpxToByteArrayInputStream(gpxParser.parse(fileInputStream));
                        }
                        catch (IOException ioe){
                            //showError
                        }
                        catch (XmlPullParserException xmlppe){
                            //showError
                        }
                    }
                });
        if (PermissionUtils.shouldAskForPermissions()){
            requestPermissionLauncher =
                    registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (!isGranted) {
                            Toast.makeText(getApplicationContext(), "Autorizzazione negata.", Toast.LENGTH_SHORT);
                            Log.w("Permessi", "Negati");
                            return;
                        }
                        startForGPXResult.launch(null);

                    });
        }


                prossimo.setOnClickListener(v -> {
                    //Controllo campi vuoti
                    boolean check = checkFieldsValues();
                    // prossimo.startAnimation(anim_btn);
                    if (check) {
                        int radioBtnChosenTracciatoId = radioGroupTracciato.getCheckedRadioButtonId();
                        if (radioBtnChosenTracciatoId == R.id.radioBtnNonInserire) {

                        } else if (radioBtnChosenTracciatoId == R.id.radioBtnMappa) {
                            startForMapResult.launch
                                    (new Intent(ControllerAddItin.this, MapActivity.class));

                        } else if (radioBtnChosenTracciatoId == R.id.radioBtnGPX) {
                            if(ContextCompat.checkSelfPermission(ControllerAddItin.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                                startForGPXResult.launch(null);
                            }
                            else {
                                askForMemoryPermissions();
                            }
                        }
                    }

                });

        annulla.setOnClickListener(v -> {
            // prossimo.startAnimation(anim_btn);
            startActivity(new Intent(ControllerAddItin.this, Controller_Home.class));
        });

    }

    private boolean checkFieldsValues() {

        String S_nome_percorso = nome_precorso.getText().toString().trim();
        String S_ore = ore.getText().toString().trim();
        String S_minuti = minuti.getText().toString().trim();
        String S_punto_inizio = punto_inizio.getText().toString().trim();
        String S_descrizione = descrizione.getText().toString().trim();

        //Scelta radiobutton
        // get selected radio button from radioGroup
        int selectedId = radioGroupDifficolta.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        radioBtnChosenDifficolta = (RadioButton) findViewById(selectedId);

        String S_diff = (String) radioBtnChosenDifficolta.getText();
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

    private void askForMemoryPermissions(){
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }




}
