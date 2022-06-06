package com.example.natour21.chat.views;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.R;


public class ViewHolderChat extends RecyclerView.ViewHolder {
    private TextView textViewNome;
    private ConstraintLayout constraintLayout;

    public ViewHolderChat(@NonNull View itemView) {
        super(itemView);
        textViewNome = itemView.findViewById(R.id.textViewChatName);
        constraintLayout = itemView.findViewById(R.id.cardListChat);
    }

    static ViewHolderChat create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ViewHolderChat(view);
    }

    public void bind(String txtNome, double unreadMessageCount){
        textViewNome.setText(txtNome);
        if (unreadMessageCount > 0) textViewNome.setTypeface(null, Typeface.BOLD);
    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }
}
