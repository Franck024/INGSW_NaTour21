package com.example.natour21.controllers.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import com.example.natour21.R;

import java.util.LinkedList;
import java.util.List;


public class DialogFragmentControllerConfirmationCode extends DialogFragment {
    private EditText editTextOTP1, editTextOTP2, editTextOTP3, editTextOTP4,
            editTextOTP5, editTextOTP6;
    private TextView textViewError;
    private List<EditText> listOTPEditText = new LinkedList<>();

    private OnSuccessfulCodeConfirmationDismissListener onSuccessfulCodeConfirmationDismissListener;

    public interface OnSuccessfulCodeConfirmationDismissListener{
        public void onSuccessfulCodeConfirmationDismiss(String code);
    }

    public DialogFragmentControllerConfirmationCode(OnSuccessfulCodeConfirmationDismissListener onSuccessfulCodeConfirmationDismissListener){
        this.onSuccessfulCodeConfirmationDismissListener = onSuccessfulCodeConfirmationDismissListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirmation_code, null);
        editTextOTP1 = dialogView.findViewById(R.id.editTextOTP1);
        editTextOTP2 = dialogView.findViewById(R.id.editTextOTP2);
        editTextOTP3 = dialogView.findViewById(R.id.editTextOTP3);
        editTextOTP4 = dialogView.findViewById(R.id.editTextOTP4);
        editTextOTP5 = dialogView.findViewById(R.id.editTextOTP5);
        editTextOTP6 = dialogView.findViewById(R.id.editTextOTP6);
        textViewError = dialogView.findViewById(R.id.textViewError);

        editTextOTP1.addTextChangedListener(new EditTextOTPWatcher(null, editTextOTP2));
        editTextOTP2.addTextChangedListener(new EditTextOTPWatcher(editTextOTP1, editTextOTP3));
        editTextOTP3.addTextChangedListener(new EditTextOTPWatcher(editTextOTP2, editTextOTP4));
        editTextOTP4.addTextChangedListener(new EditTextOTPWatcher(editTextOTP3, editTextOTP5));
        editTextOTP5.addTextChangedListener(new EditTextOTPWatcher(editTextOTP4, editTextOTP6));
        editTextOTP6.addTextChangedListener(new EditTextOTPWatcher(editTextOTP5, null));

        editTextOTP1.setOnKeyListener(new EditTextOTPListener(editTextOTP1, null));
        editTextOTP2.setOnKeyListener(new EditTextOTPListener(editTextOTP2, editTextOTP1));
        editTextOTP3.setOnKeyListener(new EditTextOTPListener(editTextOTP3, editTextOTP2));
        editTextOTP4.setOnKeyListener(new EditTextOTPListener(editTextOTP4, editTextOTP3));
        editTextOTP5.setOnKeyListener(new EditTextOTPListener(editTextOTP5, editTextOTP4));
        editTextOTP6.setOnKeyListener(new EditTextOTPListener(editTextOTP6, editTextOTP5));

        listOTPEditText.add(editTextOTP1);
        listOTPEditText.add(editTextOTP2);
        listOTPEditText.add(editTextOTP3);
        listOTPEditText.add(editTextOTP4);
        listOTPEditText.add(editTextOTP5);
        listOTPEditText.add(editTextOTP6);

        builder.setView(dialogView)
                .setPositiveButton("Invia", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }


    //Aggiungiamo questo override per evitare che al click di "Invia"
    //si chiuda il dialogo.
    //L'implementazione di Android del dialog fa sì che quando viene chiamato
    //onCreate() venga aggiunto un clickListener ai tasti che chiama dismiss()
    //quindi in onResume() sostituiamo subito questo listener per tenere il dialog attivo.
    @Override
    public void onResume()
    {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if(dialog != null)
        {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onInviaClick();
                }
            });
        }
    }

    private void onInviaClick(){
        String code = "";
        getActivity().runOnUiThread( () -> textViewError.setVisibility(View.INVISIBLE));
        for (EditText editText : listOTPEditText){
            String text = editText.getText().toString();
            if (text.isEmpty()){
                editText.requestFocus();
                return;
            }
            code += text;
        }
        onSuccessfulCodeConfirmationDismissListener.onSuccessfulCodeConfirmationDismiss(code);
    }

    public void setTextViewErrorTextAndShow(String text){
        textViewError.setText(text);
        textViewError.setVisibility(View.VISIBLE);
    }

    private class EditTextOTPListener implements View.OnKeyListener {
        private EditText thisEditText, previousEditText;

        public EditTextOTPListener(EditText thisEditText, EditText previousEditText){
            this.thisEditText = thisEditText;
            this.previousEditText = previousEditText;
        }

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            if (previousEditText == null) return false;
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL
                    && thisEditText.getText().toString().isEmpty()){
                previousEditText.setText("");
                previousEditText.requestFocus();
                return true;
            }
            return false;
        }
    }

    private class EditTextOTPWatcher implements TextWatcher{
        private EditText nextEditText, previousEditText;
        public EditTextOTPWatcher(EditText previousEditText,
                                  EditText nextEditText){
            this.previousEditText = previousEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            if (text.length() == 1 && nextEditText != null){
                nextEditText.requestFocus();
            }
        }
    }
}
