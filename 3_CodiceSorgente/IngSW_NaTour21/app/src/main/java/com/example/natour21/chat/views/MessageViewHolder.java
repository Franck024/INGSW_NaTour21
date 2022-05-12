package com.example.natour21.chat.views;

import static com.example.natour21.chat.views.AdapterMessage.VIEW_TYPE_RECEIVED;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    TextView textViewMessaggio, textViewOrario;

    public MessageViewHolder(@NonNull View itemView, int value) {
        super(itemView);
        if (value == VIEW_TYPE_RECEIVED) {
            textViewMessaggio = itemView.findViewById(R.id.textMessageReceive);
            textViewOrario = itemView.findViewById(R.id.orarioR);
        } else {
            textViewMessaggio = itemView.findViewById(R.id.textMessage);
            textViewOrario = itemView.findViewById(R.id.orario);
        }
    }

    static MessageViewHolder create(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_RECEIVED){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_container_received_message, parent, false);
        }
        else view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_container_sent_message, parent, false);
        return new MessageViewHolder(view, viewType);
    }

    public void bind(String testo, String orario){
        textViewMessaggio.setText(testo);
        textViewOrario.setText(orario);
    }
}
