package com.example.natour21.controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.natour21.addressautosearchcomponent.AddressAutoSearchComponent;
import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOItinerario;
import com.example.natour21.permissions.PermissionUtils;
import com.example.natour21.R;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.enums.DifficoltaItinerario;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.exceptions.InvalidGeoPointStringFormatException;
import com.example.natour21.map.ControllerMap;
import com.example.natour21.map.MapConverterUtils;
import com.example.natour21.work.WorkerTrackUpload;
import com.example.natour21.sharedprefs.UserSessionManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;

import io.reactivex.rxjava3.core.Observable;

public class ControllerAddNewItinerario extends AppCompatActivity implements java.util.Observer {
    private Button btnAnnulla, btnProssimo;
    private EditText editTextNomePercorso, editTextOre, editTextDurataMinuti, editTextPuntoIniziale, editTextDescrizione;
    private RadioGroup radioGroupDifficolta;
    private RadioGroup radioGroupTracciato;
    private RadioButton radioBtnChosenDifficolta;
    private RadioButton radioBtnChosenTracciato;
    private ProgressBar progressBar;
    private ByteArrayInputStream tracciatoToUpload = null;
    private ActivityResultLauncher<Void> startForGPXResult;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> startForMapResult;
    private AddressAutoSearchComponent addressAutoSearchComponent;
    private GeoPoint puntoIniziale;
    private String address;
    private Itinerario chosenItinerario;
    private final int MAX_RESULTS = 5;

    private DAOItinerario DAOItinerario;


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

        try{
            DAOItinerario = DAOFactory.getDAOItinerario();
        }
        catch (InvalidConnectionSettingsException icse){
            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                    (getApplicationContext(),"AddNewItinerario", "Impossibile aggiungere un itinerario", icse);
            finish();
        }
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                puntoIniziale = addressAutoSearchComponent.getChosenGeoPoint(position);
                address = addressAutoSearchComponent.getChosenAddressString(position);
            }
        };

        addressAutoSearchComponent = new AddressAutoSearchComponent(getApplicationContext(),
                onItemClickListener,
                MAX_RESULTS);

        addressAutoSearchComponent.addObserver(this);

        setContentView(R.layout.activity_aggiungi_itinerario);

        //Sostituzione dell'EditText con la nostra AutoCompleteTextView
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
                        tracciatoToUpload =  new ByteArrayInputStream
                                (geoPointListString.getBytes(MapConverterUtils.getCHARSET()));
                        try{
                            puntoIniziale = MapConverterUtils.getPuntoInizialeFromByteArrayInputStream(tracciatoToUpload);
                            showAccessibilityAlertDialogAndThenUpload(true);
                        }
                        catch (InvalidGeoPointStringFormatException igpsfe){
                            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                                    (getApplicationContext(),"AddNewItinerario", "Errore nella conversione del tracciato.", igpsfe);

                        }
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
                            Gpx gpx = gpxParser.parse(fileInputStream);
                            if (gpx.getTracks().size() != 1) {
                                ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                                        (getApplicationContext(),"AddNewItinerario",
                                                "Il file .gpx scelto ha più di un tracciato. " +
                                                        "Solo un tracciato può essere associato ad un itinerario.", null);
                                return;
                            }
                            tracciatoToUpload = MapConverterUtils.gpxToByteArrayInputStream(gpx);
                            puntoIniziale = MapConverterUtils.getPuntoInizialeFromByteArrayInputStream(tracciatoToUpload);
                            showAccessibilityAlertDialogAndThenUpload(true);
                        }
                        catch (IOException | XmlPullParserException | InvalidGeoPointStringFormatException e){
                            ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                                    (getApplicationContext(),"AddNewItinerario", "Errore nella conversione del tracciato.",
                                    e);
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
                    boolean check = checkFieldsValuesAndIfValidSetItinerario(radioBtnChosenTracciatoId);
                    // prossimo.startAnimation(anim_btn);
                    if (!check) return;
                    if (radioBtnChosenTracciatoId == R.id.radioBtnNonInserire) {
                        showAccessibilityAlertDialogAndThenUpload(false);
                    } else if (radioBtnChosenTracciatoId == R.id.radioBtnMappa) {
                        Intent intent = new Intent(ControllerAddNewItinerario.this,
                                ControllerMap.class);
                        if (addressAutoSearchComponent.isResultReady()){
                            intent.putExtra("PUNTO_INIZIALE_LAT", puntoIniziale.getLatitude());
                            intent.putExtra("PUNTO_INIZIALE_LONG", puntoIniziale.getLongitude());
                        }
                        startForMapResult.launch
                                (intent);
                    } else if (radioBtnChosenTracciatoId == R.id.radioBtnGPX) {
                        if(ContextCompat.checkSelfPermission(ControllerAddNewItinerario.this,
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
            startActivity(new Intent(ControllerAddNewItinerario.this, ControllerHome.class));
        });

    }

    @Override
    public void update(java.util.Observable observable, Object o) {
        Log.i("AddNewItinerario", "Observable update received");
        if (!(o instanceof Boolean)) return;
        if (progressBar == null) return;
        Boolean isSearchingAddress = (Boolean) o;
        runOnUiThread( () -> progressBar.setVisibility
                (isSearchingAddress ? View.VISIBLE : View.INVISIBLE));
    }

    private boolean checkFieldsValuesAndIfValidSetItinerario(int tracciatoChoice) {

        String nomePercorso = editTextNomePercorso.getText().toString().trim();
        String ore = editTextOre.getText().toString().trim();
        String minuti = editTextDurataMinuti.getText().toString().trim();
        String descrizione = editTextDescrizione.getText().toString().trim();
        if (descrizione != null && descrizione.equals("")) descrizione = null;

        //Scelta radiobutton
        int selectedId = radioGroupDifficolta.getCheckedRadioButtonId();
        radioBtnChosenDifficolta = (RadioButton) findViewById(selectedId);

        DifficoltaItinerario difficolta = DifficoltaItinerario.valueOf
                (radioBtnChosenDifficolta.getText().toString().toLowerCase());
        if (nomePercorso.isEmpty()) {
            editTextNomePercorso.setError("Campo obbligatorio.");
            editTextNomePercorso.requestFocus();
            return false;
        }

        if (ore.isEmpty() && minuti.isEmpty()) {
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
        Double pointLat = null, pointLong = null;
        if (puntoIniziale != null){
            pointLat = puntoIniziale.getLatitude();
            pointLong = puntoIniziale.getLongitude();
        }
        int durata = Integer.parseInt(ore) * 60 + Integer.parseInt(minuti);
        chosenItinerario = new Itinerario(0, UserSessionManager.getInstance().getUserId(),
                nomePercorso, pointLat, pointLong, address,
                durata, difficolta, descrizione, null, null, null);
        return true;
    }
    private boolean verificaNumerica(String text) {
        return text.matches("[0-9]+");
    }

    private void askForMemoryPermissions(){
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void showAccessibilityAlertDialogAndThenUpload(boolean isTracciatoPresent){
        AlertDialog.Builder builder = new AlertDialog.Builder(ControllerAddNewItinerario.this);
        builder.setTitle("Accessibilità");

        String[] items = {"Questo percorso è accessibile a persone con disabilità motorie.",
                "Questo percorso è accessibile a persone con disabilità visive."};
        boolean[] isCheckedArray = {false, false};
        builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                isCheckedArray[which] = isChecked;
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chosenItinerario.setAccessibleMobilityImpairment(isCheckedArray[0]);
                chosenItinerario.setAccessibleVisualImpairment(isCheckedArray[1]);
                if (isTracciatoPresent)  uploadItinerarioWithTracciato();
                else uploadItinerarioWithoutTracciato();
                startActivity(new Intent(ControllerAddNewItinerario.this, ControllerHome.class));
            }
        });
        builder.setNegativeButton("Annulla", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void uploadItinerarioWithTracciato(){
        Callable<String> getAddressNameCallable = () -> {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> results = geocoder.getFromLocation
                    (puntoIniziale.getLatitude(), puntoIniziale.getLongitude(), 1);
            if (results == null || results.isEmpty()) return null;
            else return results.get(0).getAddressLine(0);
        };

        Observable.fromCallable(getAddressNameCallable)
                .map(nomePuntoIniziale -> {
                    chosenItinerario.setNomePuntoIniziale(nomePuntoIniziale);
                    buildAndEnqueueWorkRequest();
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trueValue -> {}, error ->
                        ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                                (getApplicationContext(),"AddNewItinerario", "Impossibile caricare l'itinerario", error));
    }

    private void uploadItinerarioWithoutTracciato(){
        Callable<Void> insertItinerarioCallable = () -> {
            DAOItinerario.insertItinerario(chosenItinerario);
            return null;
        };
        Completable.fromCallable(insertItinerarioCallable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( () -> Toast
                                .makeText(getApplicationContext(), "Itinerario caricato con successo.", Toast.LENGTH_SHORT)
                                .show(),
                        error -> ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                                (getApplicationContext(),"AddNewItinerario", "Impossibile caricare l'itinerario.", error));
    }

    private void buildAndEnqueueWorkRequest() throws JsonProcessingException {
        String inputItinerario = new ObjectMapper().writeValueAsString(chosenItinerario);
        byte[] tracciato = new byte[tracciatoToUpload.available()];
        tracciatoToUpload.read(tracciato, 0, tracciato.length);
        tracciatoToUpload.reset();
        Data data = new Data.Builder()
                .putString("ITINERARIO", inputItinerario)
                .putByteArray("TRACCIATO", tracciato)
                .build();
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(WorkerTrackUpload.class)
                .setInputData(data)
                .setConstraints(constraints)
                .build();
        workManager.enqueue(uploadWorkRequest);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (addressAutoSearchComponent != null) addressAutoSearchComponent.deleteObserver(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (chosenItinerario == null) return;
        String stringItinerario;
        try{
            stringItinerario = new ObjectMapper().writeValueAsString(chosenItinerario);
        } catch (JsonProcessingException jpe){
            Log.e("AddNewItinerario", "onSaveInstanceState can't parse itinerario", jpe);
            return;
        }
        outState.putString("ITINERARIO", stringItinerario);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (!savedInstanceState.containsKey("ITINERARIO")) return;
        String stringItinerario = savedInstanceState.getString("ITINERARIO");
        try{
            chosenItinerario = new ObjectMapper().readValue(stringItinerario, Itinerario.class);
        }
        catch (JsonProcessingException jpe){
            Log.e("AddNewItinerario", "onRestoreIstanceState can't map itinerario", jpe);
            return;
        }
    }
}
