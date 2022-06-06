package com.example.natour21.chat.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.R;
import com.example.natour21.chat.room.entities.ChatDBEntity;
import com.example.natour21.chat.room.entities.MessaggioDBEntity;
import com.example.natour21.entities.Messaggio;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class AdapterMessage extends ListAdapter<MessaggioDBEntity, MessageViewHolder> {


    public static final int VIEW_TYPE_RECEIVED = 1;
    public static final int VIEW_TYPE_SENT = 2;

    public AdapterMessage(@NonNull DiffUtil.ItemCallback<MessaggioDBEntity> diffCallback){
        super(diffCallback);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MessageViewHolder.create(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessaggioDBEntity messaggio = getItem(position);
        String testo = messaggio.getText();
        String timestamp = messaggio.getTimestamp().format(DateTimeFormatter
                .ofPattern("HH:mm - dd/MM/yy")
                .withLocale(Locale.getDefault()));
        holder.bind(testo, timestamp);
    }

    @Override
    public int getItemViewType(int position){
        if (getItem(position).isMainUtenteSender()){
            return VIEW_TYPE_SENT;
        }
        else return VIEW_TYPE_RECEIVED;
    }

    public static class MessageDiff extends DiffUtil.ItemCallback<MessaggioDBEntity> {

        @Override
        public boolean areItemsTheSame(@NonNull MessaggioDBEntity oldItem, @NonNull MessaggioDBEntity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MessaggioDBEntity oldItem, @NonNull MessaggioDBEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }

}
