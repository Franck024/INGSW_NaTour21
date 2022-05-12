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


public class ChatViewHolder extends RecyclerView.ViewHolder {
    TextView textViewNome;
    ConstraintLayout mainLayout;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewNome = itemView.findViewById(R.id.nomeChat);
        mainLayout = itemView.findViewById(R.id.cardListChat);
    }

    static ChatViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_chat, parent, false);
        return new ChatViewHolder(view);
    }

    public void bind(String txtNome, double unreadMessageCount){
        textViewNome.setText(txtNome);
        if (unreadMessageCount > 0) textViewNome.setTypeface(null, Typeface.BOLD);
    }

    public ConstraintLayout getMainLayout() {
        return mainLayout;
    }
}
