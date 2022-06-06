package com.example.natour21.chat.views;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.natour21.chat.room.entities.MessaggioDBEntity;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AdapterMessaggio extends ListAdapter<MessaggioDBEntity, ViewHolderMessaggio> {


    //Non si usa un enum perché getItemViewType restituisce int.
    public static final int VIEW_TYPE_RECEIVED = 1;
    public static final int VIEW_TYPE_SENT = 2;

    public AdapterMessaggio(@NonNull DiffUtil.ItemCallback<MessaggioDBEntity> diffCallback){
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolderMessaggio onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolderMessaggio.create(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMessaggio holder, int position) {
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

    public static class MessaggioDiff extends DiffUtil.ItemCallback<MessaggioDBEntity> {

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
