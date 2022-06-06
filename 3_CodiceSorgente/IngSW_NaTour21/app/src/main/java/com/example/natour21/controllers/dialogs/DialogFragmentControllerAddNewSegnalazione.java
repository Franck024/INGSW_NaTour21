package com.example.natour21.controllers.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOSegnalazione;
import com.example.natour21.R;
import com.example.natour21.entities.Segnalazione;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.exceptions.WrappedCRUDException;

import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DialogFragmentControllerAddNewSegnalazione extends DialogFragment {

    private EditText editTextTitolo, editTextDescrizione;
    long idItinerario;
    private String segnalazioneAuthorId;


    public DialogFragmentControllerAddNewSegnalazione(long idItinerario, String segnalazioneAuthorId){
        this.idItinerario = idItinerario;
        this.segnalazioneAuthorId = segnalazioneAuthorId;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_segnalazione, null);
        editTextTitolo = dialogView.findViewById(R.id.editTextTitolo);
        editTextDescrizione = dialogView.findViewById(R.id.editTextDescrizione);
        builder.setView(dialogView)
                .setPositiveButton("Invia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            insertSegnalazione();
                        }
                        catch (InvalidConnectionSettingsException | WrappedCRUDException exception){
                            Context context = getActivity().getApplicationContext();
                            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
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
        DAOSegnalazione DAOSegnalazione = DAOFactory.getDAOSegnalazione();
        Segnalazione segnalazione = new Segnalazione(segnalazioneAuthorId,
                idItinerario,
                editTextTitolo.getText().toString(),
                editTextDescrizione.getText().toString());

        Callable<Void> insertCallable = () ->{
            DAOSegnalazione.insertSegnalazione(segnalazione);
            return null;
        };

        Completable.fromCallable(insertCallable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( () -> Toast.makeText(requireActivity().getApplicationContext(),
                        "Segnalazione inserita!", Toast.LENGTH_SHORT).show(), error -> {
                    if (error instanceof WrappedCRUDException) throw error;
                    Toast.makeText(requireActivity().getApplicationContext(),
                            "Impossibile inserire la segnalazione.", Toast.LENGTH_SHORT).show();
                    Log.e("Segnalazione", error.getMessage(), error);
                });
    }
}
