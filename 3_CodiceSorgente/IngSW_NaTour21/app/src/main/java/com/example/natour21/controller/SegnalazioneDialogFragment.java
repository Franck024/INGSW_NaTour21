package com.example.natour21.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOSegnalazione;
import com.example.natour21.R;
import com.example.natour21.entities.Segnalazione;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.exceptions.WrappedCRUDException;

public class SegnalazioneDialogFragment extends DialogFragment {

    EditText editTextTitolo, editTextDescrizione, editTextAggDurataOre, editTextAggDurataMinuti;
    long idItinerario;
    String segnalazioneAuthorId;


    public SegnalazioneDialogFragment(long idItinerario, String segnalazioneAuthorId){
        this.idItinerario = idItinerario;
        this.segnalazioneAuthorId = segnalazioneAuthorId;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.popup_segnalazione, null))
                .setPositiveButton("Invia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            insertSegnalazione();
                        }
                        catch (InvalidConnectionSettingsException | WrappedCRUDException exception){
                            Context context = getActivity().getApplicationContext();
                            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG);
                        }

                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void insertSegnalazione() throws InvalidConnectionSettingsException, WrappedCRUDException {
        if(controlloCampi() == true ) {
            DAOSegnalazione DAOSegnalazione = DAOFactory.getDAOSegnalazione();
            if(!editTextAggDurataMinuti.getText().toString().isEmpty() &&
                    !editTextAggDurataOre.getText().toString().isEmpty()){

              /*  Segnalazione segnalazione = new Segnalazione(segnalazioneAuthorId,
                        idItinerario,
                        editTextTitolo.getText().toString(),
                        editTextDescrizione.getText().toString());
                        editTextAggDurataOre.getText().toString();
                        editTextAggDurataMinuti.getText().toString();
                DAOSegnalazione.insertSegnalazione(segnalazione);  */
            }else{
                Segnalazione segnalazione = new Segnalazione(segnalazioneAuthorId,
                        idItinerario,
                        editTextTitolo.getText().toString(),
                        editTextDescrizione.getText().toString());
                DAOSegnalazione.insertSegnalazione(segnalazione);
            }
        }
    }

    private boolean controlloCampi(){
        if (editTextTitolo.getText().toString().isEmpty()) {
            editTextTitolo.setError("Campo obbligatorio.");
            editTextTitolo.requestFocus();
            return false;
        }
        if (editTextDescrizione.getText().toString().isEmpty()) {
            editTextDescrizione.setError("Campo obbligatorio.");
            editTextDescrizione.requestFocus();
            return false;
        }
        return true;
    }
}
