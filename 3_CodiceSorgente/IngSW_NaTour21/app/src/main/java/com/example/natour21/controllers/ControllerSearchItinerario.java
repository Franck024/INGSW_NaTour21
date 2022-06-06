package com.example.natour21.controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.AddressAutoSearchComponent;
import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOItinerario;
import com.example.natour21.DAOs.DAOUtente;
import com.example.natour21.ParentItem;
import com.example.natour21.ParentItemUtil;
import com.example.natour21.PostAdapter;
import com.example.natour21.R;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.entities.Utente;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Controller_Ricerca extends AppCompatActivity implements java.util.Observer {

    private GeoPoint selectedGeoPoint;

    private LinearLayout mainLayout, searchParamsLayout;
    private ImageButton back;
    private Button btnCerca;
    private EditText editTextRaggio, editTextOre, editTextMinuti;
    private CheckBox checkBoxFacile, checkBoxMedia, checkBoxDifficile;
    private Spinner spinnerDurata;
    private Switch switchDisabilitaMotoria, switchDisabilitaVisiva;
    private AddressAutoSearchComponent addressAutoSearchComponent;
    private AutoCompleteTextView addressAutoCompleteTextView;
    private ProgressBar progressBar;

    RecyclerView recyclerViewPosts;

    private DAOItinerario DAOItinerario;
    private DAOUtente DAOUtente;

    private final int BOX_FACILE_UNCHECKED_COLOR = Color.rgb(170, 221, 170);
    private final int BOX_FACILE_CHECKED_COLOR = Color.rgb(170, 255, 170);
    private final int BOX_MEDIA_UNCHECKED_COLOR = Color.rgb(221, 221, 170);
    private final int BOX_MEDIA_CHECKED_COLOR = Color.rgb(255, 255, 170);
    private final int BOX_DIFFICILE_UNCHECKED_COLOR = Color.rgb(221, 158, 178);
    private final int BOX_DIFFICILE_CHECKED_COLOR = Color.rgb(255, 158, 178);

    private enum RicercaState{
        INSERTING_PARAMS,
        RESULT
    }

    private RicercaState ricercaState = RicercaState.INSERTING_PARAMS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ricerca_itinerario);
        AdapterView.OnItemClickListener addressAutoSearchComponentClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedGeoPoint = addressAutoSearchComponent.getChosenGeoPoint(position);
            }
        };

        try{
            DAOItinerario = DAOFactory.getDAOItinerario();
            DAOUtente = DAOFactory.getDAOUtente();
        } catch (InvalidConnectionSettingsException icse) {
            Log.e("Ricerca -InvalidConnectionSettingsException", icse.getMessage(), icse);
        }

        mainLayout = findViewById(R.id.linearLayoutRicerca);
        searchParamsLayout = findViewById(R.id.searchParamsLayout);
        back = findViewById(R.id.back_ricerca);
        addressAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewArea);
        editTextRaggio = findViewById(R.id.editTextRaggioArea);
        editTextOre = findViewById(R.id.editTextDurataOre);
        editTextMinuti = findViewById(R.id.editTextDurataMinuti);
        checkBoxFacile = findViewById(R.id.checkBoxFacile);
        checkBoxDifficile = findViewById(R.id.checkBoxDifficile);
        checkBoxMedia = findViewById(R.id.checkBoxMedia);
        spinnerDurata = findViewById(R.id.spinnerDurata);
        switchDisabilitaMotoria = findViewById(R.id.switchDisabilitaMotoria);
        switchDisabilitaVisiva = findViewById(R.id.switchDisabilitaVisiva);
        progressBar = findViewById(R.id.progressBar);
        btnCerca = findViewById(R.id.btnCerca);

        addressAutoSearchComponent = new AddressAutoSearchComponent
                (getBaseContext(), addressAutoCompleteTextView, addressAutoSearchComponentClickListener, 5);

        addressAutoSearchComponent.addObserver(this);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back.animate().rotationY(360).withEndAction(
                        () -> goBack());
            }
        });

        checkBoxFacile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread( () -> checkBoxFacile.setBackgroundColor(checkBoxFacile.isChecked()
                        ? BOX_FACILE_CHECKED_COLOR
                        : BOX_FACILE_UNCHECKED_COLOR));

            }
        });

        checkBoxMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(() -> checkBoxMedia.setBackgroundColor(checkBoxMedia.isChecked()
                        ? BOX_MEDIA_CHECKED_COLOR
                        : BOX_MEDIA_UNCHECKED_COLOR));

            }
        });

        checkBoxDifficile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread( () -> checkBoxDifficile.setBackgroundColor(checkBoxDifficile.isChecked()
                        ? BOX_DIFFICILE_CHECKED_COLOR
                        : BOX_DIFFICILE_UNCHECKED_COLOR));

            }
        });

        btnCerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtnCercaClick();
            }
        });




    }

    @Override
    public void update(java.util.Observable observable, Object o) {
        if (!(o instanceof Boolean)) return;
        if (progressBar == null) return;
        Boolean isSearchingAddress = (Boolean) o;
        runOnUiThread( () -> progressBar.setVisibility
                (isSearchingAddress ? View.VISIBLE : View.INVISIBLE));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (addressAutoSearchComponent != null) addressAutoSearchComponent.deleteObserver(this);
    }

    private void onBtnCercaClick(){
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        Double pointLat = null, pointLong = null, distanceWithin = null;
        if (addressAutoSearchComponent.isResultReady() && selectedGeoPoint != null){
            pointLat = selectedGeoPoint.getLatitude();
            pointLong = selectedGeoPoint.getLongitude();
            try{
                distanceWithin = Double.parseDouble(editTextRaggio.getText().toString()) * 1000;
                if (distanceWithin < 0) distanceWithin = null;
            }
            catch (NumberFormatException nfe){
                distanceWithin = null;
            }
        }
        Boolean[] difficoltaArray = new Boolean[3];
        difficoltaArray[0] = checkBoxFacile.isChecked();
        difficoltaArray[1] = checkBoxMedia.isChecked();
        difficoltaArray[2] = checkBoxDifficile.isChecked();
        boolean isAllFalse = true;
        for (Boolean b : difficoltaArray){
            if (b) {
                isAllFalse = false;
                break;
            }
        }
        if (isAllFalse) difficoltaArray = null;

        Integer ore = null, minuti = null, duration = null;
        Boolean shouldBeLessThanGivenDuration = null;
        try{
            ore = Integer.parseInt(editTextOre.getText().toString());
            if (ore < 0) ore = 0;
            minuti = Integer.parseInt(editTextMinuti.getText().toString());
            if (minuti < 0) minuti = null;
            duration = (minuti != null) ? (ore*60) + minuti : null;
            shouldBeLessThanGivenDuration = spinnerDurata.getSelectedItemPosition() == 0;
        }
        catch (NumberFormatException nfe){

        }

        Boolean isAccessibleMobilityImpairment = switchDisabilitaMotoria.isChecked() ? true : null;
        Boolean isaccessibleVisualImpairment = switchDisabilitaVisiva.isChecked() ? true : null;

        Double finalPointLat = pointLat;
        Double finalPointLong = pointLong;
        Double finalDistanceWithin = distanceWithin;
        Boolean[] finalDifficoltaArray = difficoltaArray;
        Integer finalDuration = duration;
        Boolean finalShouldBeLessThanGivenDuration = shouldBeLessThanGivenDuration;
        Callable<List<ParentItem>> callable = () -> {
            List<Itinerario> itinerarioList;
            Utente utente;
            List<ParentItem> parentItems = new LinkedList<>();
            itinerarioList = DAOItinerario.getItinerarioByProperties
                    (
                            finalPointLat, finalPointLong, finalDistanceWithin,
                            finalDifficoltaArray,
                            finalDuration, finalShouldBeLessThanGivenDuration,
                            isAccessibleMobilityImpairment,
                            isaccessibleVisualImpairment
                    );
            for (Itinerario i : itinerarioList){
                utente = DAOUtente.getUtenteByEmail(i.getAuthorId());
                parentItems.add(ParentItemUtil.itinerarioToParentItem(i, utente.getDisplayName()));
            }
            return parentItems;

        };

        Observable.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultParentItems -> {
                    if (resultParentItems == null || resultParentItems.isEmpty()){
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),
                                "Nessun risultato.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Sostituzione layout ricerca con itinerari trovati
                    PostAdapter postAdapter = new PostAdapter(this, new ArrayList<>());
                    recyclerViewPosts =  new RecyclerView(getBaseContext());
                    recyclerViewPosts.setAdapter(postAdapter);
                    recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
                    RecyclerView.LayoutParams params = new RecyclerView.LayoutParams
                            (RecyclerView.LayoutParams.MATCH_PARENT,
                                    RecyclerView.LayoutParams.WRAP_CONTENT);
                    recyclerViewPosts.setLayoutParams(params);
                    postAdapter.addAll(resultParentItems);
                    hideRicercaLayout();
                    progressBar.setVisibility(View.INVISIBLE);
                }, error -> {
                    Toast.makeText(getApplicationContext(),
                            "Errore.", Toast.LENGTH_SHORT);
                    Log.e("Ricerca", error.getMessage(), error);
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();

    }

    private void goBack(){
        if (ricercaState.equals(RicercaState.INSERTING_PARAMS))
            startActivity(new Intent(Controller_Ricerca.this, Controller_Home.class));
        else showRicercaLayout();
    }

    private void showRicercaLayout(){
        if (recyclerViewPosts != null) {
            mainLayout.removeView(recyclerViewPosts);
            recyclerViewPosts = null;
        }
        ricercaState = RicercaState.INSERTING_PARAMS;
        if (searchParamsLayout == null) return;
        runOnUiThread(() -> {
            searchParamsLayout.setVisibility(View.VISIBLE);
        });

    }

    private void hideRicercaLayout(){
        if (searchParamsLayout == null) return;
        ricercaState = RicercaState.RESULT;
        runOnUiThread(() ->  {
            searchParamsLayout.setVisibility(View.GONE);
            if (recyclerViewPosts != null) mainLayout.addView(recyclerViewPosts);
        });
    }
}
