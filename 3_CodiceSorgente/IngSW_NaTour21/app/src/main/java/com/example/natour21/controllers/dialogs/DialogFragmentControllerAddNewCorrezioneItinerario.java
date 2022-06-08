package com.example.natour21.controllers.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOCorrezioneItinerario;
import com.example.natour21.R;
import com.example.natour21.entities.CorrezioneItinerario;
import com.example.natour21.enums.DifficoltaItinerario;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.exceptions.WrappedCRUDException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DialogFragmentControllerAddNewCorrezioneItinerario extends DialogFragment {
    private EditText editTextDurataOre, editTextDurataMinuti;
    private Spinner spinnerDifficoltaItinerario;

    private Integer durata;
    private DifficoltaItinerario difficoltaItinerario;

    public interface OnPositiveButtonClickListener{
        public void onPositiveButtonClick(boolean areFieldsProperlyFilled,
                                          Integer durata, DifficoltaItinerario difficoltaItinerario);
    }

    private OnPositiveButtonClickListener onPositiveButtonClick;

    public DialogFragmentControllerAddNewCorrezioneItinerario(OnPositiveButtonClickListener onPositiveButtonClick){
       this.onPositiveButtonClick = onPositiveButtonClick;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_correzione_itinerario, null);
        editTextDurataOre = dialogView.findViewById(R.id.editTextDurataOre);
        editTextDurataMinuti = dialogView.findViewById(R.id.editTextDurataMinuti);
        spinnerDifficoltaItinerario = dialogView.findViewById(R.id.spinnerDifficolta);
        builder.setView(dialogView)
                .setPositiveButton("Invia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        onPositiveButtonClick.onPositiveButtonClick(checkFieldsAndSetVariables(), durata, difficoltaItinerario);
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private boolean checkFieldsAndSetVariables(){
        Integer ore = null, minuti = null;
        try{
            try{
                ore = Integer.parseInt(editTextDurataOre.getText().toString());
                if (ore < 0) ore = 0;
            }
            catch (NumberFormatException nfeore) {
                ore = 0;
            }
            minuti = Integer.parseInt(editTextDurataMinuti.getText().toString());
            if (minuti < 0) minuti = null;
            durata = (minuti != null) ? (ore*60) + minuti : null;
            if (durata != null && durata.equals(0)) durata = null;
        }
        catch (NumberFormatException nfeminuti){
            //la durata rimane null, quindi non c'è bisogno di fare nulla
        }
        int selectedItem = spinnerDifficoltaItinerario.getSelectedItemPosition();
        if (selectedItem == 0) difficoltaItinerario = null;
        else difficoltaItinerario = DifficoltaItinerario.values()[selectedItem-1];

        if (difficoltaItinerario == null && durata == null){
            return false;
        }
        return true;
    }
}
