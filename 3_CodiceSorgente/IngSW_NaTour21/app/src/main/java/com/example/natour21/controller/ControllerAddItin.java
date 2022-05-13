package com.example.natour21.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.example.natour21.AddressAutoSearchComponent;
import com.example.natour21.PermissionUtils;
import com.example.natour21.R;
import com.example.natour21.map.MapActivity;
import com.example.natour21.map.MapConverter;

import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

import io.ticofab.androidgpxparser.parser.GPXParser;

public class ControllerAddItin extends AppCompatActivity implements java.util.Observer {
    private Button btnAnnulla, btnProssimo;
    private EditText editTextNomePercorso, editTextOre, editTextDurataMinuti, editTextPuntoIniziale, editTextDescrizione;
    private RadioGroup radioGroupDifficolta;
    private RadioGroup radioGroupTracciato;
    private RadioButton radioBtnChosenDifficolta;
    private RadioButton radioBtnChosenTracciato;
    private ProgressBar progressBar;
    private ByteArrayInputStream itinerarioToUpload = null;
    private ActivityResultLauncher<Void> startForGPXResult;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> startForMapResult;
    private AddressAutoSearchComponent addressAutoSearchComponent;
    private GeoPoint puntoIniziale;
    private final int MAX_RESULTS = 5;


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

        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                puntoIniziale = addressAutoSearchComponent.getClickedGeoPoint(position);
            }
        };

        addressAutoSearchComponent = new AddressAutoSearchComponent(getApplicationContext(),
                onItemClickListener,
                MAX_RESULTS);

        addressAutoSearchComponent.addObserver(this);

        setContentView(R.layout.frame_aggiungi_itinerario);
        editTextPuntoIniziale = findViewById(R.id.autoCompleteEditTextView);
        editTextPuntoIniziale.setVisibility(View.GONE);

        AutoCompleteTextView autoCompleteTextView = addressAutoSearchComponent
                .getAddressAutoCompleteTextView();

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayoutAggiungiItinerario);

        constraintLayout.addView(addressAutoSearchComponent.getAddressAutoCompleteTextView());

        autoCompleteTextView.setId(editTextPuntoIniziale.getId());
        autoCompleteTextView.setLayoutParams(editTextPuntoIniziale.getLayoutParams());
        autoCompleteTextView.setBackground(editTextPuntoIniziale.getBackground());
        autoCompleteTextView.setInputType(editTextPuntoIniziale.getInputType());


        btnProssimo = findViewById(R.id.btnProssimo);
        btnAnnulla = findViewById(R.id.btnIndietro);
        editTextNomePercorso = findViewById(R.id.editTextNome);
        editTextOre = findViewById(R.id.editTextDurataOre);
        editTextDurataMinuti = findViewById(R.id.editTextDurataMinuti);
        editTextDescrizione = findViewById(R.id.editTextDescrizione);
        radioGroupDifficolta = findViewById(R.id.radioGroupDifficolta);
        radioGroupTracciato = findViewById(R.id.radioBtnGroupTracciato);
        progressBar = findViewById(R.id.progressBarPuntoIniziale);

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
                        if (!extension.equals("gpx")) return;
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
                            Log.w("Permessi", "Permessi di lettura negati");
                            return;
                        }
                        startForGPXResult.launch(null);

                    });
        }


                btnProssimo.setOnClickListener(v -> {
                    //Controllo campi vuoti
                    int radioBtnChosenTracciatoId = radioGroupTracciato.getCheckedRadioButtonId();
                    boolean check = checkFieldsValues(radioBtnChosenTracciatoId);
                    // prossimo.startAnimation(anim_btn);
                    if (!check) return;
                    if (radioBtnChosenTracciatoId == R.id.radioBtnNonInserire) {

                    } else if (radioBtnChosenTracciatoId == R.id.radioBtnMappa) {
                        Intent intent = new Intent(ControllerAddItin.this,
                                MapActivity.class);
                        if (addressAutoSearchComponent.isResultReady()){
                            intent.putExtra("PUNTO_INIZIALE_LAT", puntoIniziale.getLatitude());
                            intent.putExtra("PUNTO_INIZIALE_LONG", puntoIniziale.getLongitude());
                        }
                        startForMapResult.launch
                                (intent);
                    } else if (radioBtnChosenTracciatoId == R.id.radioBtnGPX) {
                        if(ContextCompat.checkSelfPermission(ControllerAddItin.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                            startForGPXResult.launch(null);
                        }
                        else {
                            askForMemoryPermissions();
                        }
                    }
                });

        btnAnnulla.setOnClickListener(v -> {
            // prossimo.startAnimation(anim_btn);
            startActivity(new Intent(ControllerAddItin.this, Controller_Home.class));
        });

    }

    @Override
    public void update(Observable observable, Object o) {
        Log.i("AddItin", "Observable update received");
        if (!(o instanceof Boolean)) return;
        if (progressBar == null) return;
        Boolean isSearchingAddress = (Boolean) o;
        runOnUiThread( () -> progressBar.setVisibility
                (isSearchingAddress ? View.VISIBLE : View.INVISIBLE));
    }

    private boolean checkFieldsValues(int tracciatoChoice) {

        String nomePercorso = editTextNomePercorso.getText().toString().trim();
        String ore = editTextOre.getText().toString().trim();
        String minuti = editTextDurataMinuti.getText().toString().trim();
        String puntoIniziale = editTextPuntoIniziale.getText().toString().trim();
        String descrizione = editTextDescrizione.getText().toString().trim();

        //Scelta radiobutton
        // get selected radio button from radioGroup
        int selectedId = radioGroupDifficolta.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        radioBtnChosenDifficolta = (RadioButton) findViewById(selectedId);

        String difficolta = radioBtnChosenDifficolta.getText().toString();
        /// fine

        if (nomePercorso.isEmpty()) {
            editTextNomePercorso.setError("Campo obbligatorio.");
            editTextNomePercorso.requestFocus();
            return false;
        }

        if (ore.isEmpty()) {
            editTextOre.setError("Campo obbligatorio.");
            editTextOre.requestFocus();
            return false;
        }

        if (minuti.isEmpty()) {
            editTextDurataMinuti.setError("Campo obbligatorio.");
            editTextDurataMinuti.requestFocus();
            return false;
        }

        if (!addressAutoSearchComponent.isResultReady() && tracciatoChoice == R.id.radioBtnNonInserire) {
            addressAutoSearchComponent.getAddressAutoCompleteTextView().setError("Campo obbligatorio.");
            addressAutoSearchComponent.getAddressAutoCompleteTextView().requestFocus();
            return false;
        }

        if(verificaNumerica(minuti)==false){
            editTextDurataMinuti.setError("Inserisci un numero");
            editTextDurataMinuti.requestFocus();
            return false;
        }

        if(verificaNumerica(ore)==false){
            editTextOre.setError("Inserisci un numero");
            editTextOre.requestFocus();
            return false;
        }
        return true;
    }
    private boolean verificaNumerica(String text) {
        return text.matches("[0-9]+") && text.length() < 3;
    }

    private void askForMemoryPermissions(){
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (addressAutoSearchComponent != null) addressAutoSearchComponent.deleteObserver(this);
    }




}
