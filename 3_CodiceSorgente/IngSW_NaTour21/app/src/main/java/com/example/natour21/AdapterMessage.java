package com.example.natour21;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.natour21.controller.Controller_ChatUtente;
import com.example.natour21.entities.Messaggio;

import java.util.ArrayList;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.MyViewHolder>{
    ArrayList<Messaggio> messageItemArrayList;
    Context context;

    public AdapterMessage(Context ct, ArrayList<Messaggio> messageItemArrayList){
        context =  ct;
        this.messageItemArrayList = messageItemArrayList;
    }

    @NonNull
    @Override
    public AdapterMessage.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_container_sent_message, parent, false);
        return new AdapterMessage.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMessage.MyViewHolder holder, int position) {
        Messaggio messageChat = messageItemArrayList.get(position);

        holder.messaggio.setText(messageChat.getTesto());
        holder.orario.setText("12.22");  // messageChat.getTimestamp()
    }

    @Override
    public int getItemCount() {
        return messageItemArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView messaggio, orario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            messaggio = itemView.findViewById(R.id.textMessage);
            orario = itemView.findViewById(R.id.orario);
        }
    }

    //// TEST

    //FINE
}