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
import com.example.natour21.enums.DifficoltaItinerario;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.exceptions.WrappedCRUDException;

import java.util.concurrent.Callable;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DialogFragmentControllerAddNewSegnalazione extends DialogFragment {

    private EditText editTextTitolo, editTextDescrizione;

    public interface OnPositiveButtonClickListener{
        public void onPositiveButtonClick(boolean areFieldsProperlyFilled,
                                          String segnalazioneTitolo, String segnalazioneDescrizione);
    }

    private OnPositiveButtonClickListener onPositiveButtonClickListener;

    public DialogFragmentControllerAddNewSegnalazione(OnPositiveButtonClickListener onPositiveButtonClickListener){
        this.onPositiveButtonClickListener = onPositiveButtonClickListener;
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
                        String segnalazioneTitolo = editTextTitolo.getText().toString();
                        String segnalazioneDescrizione = editTextDescrizione.getText().toString();
                        onPositiveButtonClickListener.onPositiveButtonClick(
                                !segnalazioneTitolo.isEmpty(),
                                segnalazioneTitolo, segnalazioneDescrizione);

                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });
        return builder.create();
    }

}
