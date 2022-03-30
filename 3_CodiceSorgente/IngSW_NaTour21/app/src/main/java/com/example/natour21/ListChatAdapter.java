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
import com.example.natour21.controller.Controller_itinerario;
import java.util.ArrayList;
import java.util.List;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.MyViewHolder>{
    ArrayList<UtentiChat> chatItemArrayList;
    Context context;

    public ListChatAdapter(Context ct, ArrayList<UtentiChat> chatItemArrayList){
        context =  ct;
        this.chatItemArrayList = chatItemArrayList;
    }

    @NonNull
    @Override
    public ListChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_chat, parent, false);
        return new ListChatAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListChatAdapter.MyViewHolder holder, int position) {
        UtentiChat utentiChat = chatItemArrayList.get(position);

        holder.txtNome.setText(utentiChat.getNome());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Controller_ChatUtente.class);
                // Intent putExtra 5.22 https://youtu.be/xgpLYwEmlO0
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatItemArrayList.size();
    }
/*
    public void addAll(List<UtentiChat> utentiItem){
        chatItemArrayList.addAll(utentiItem);
        System.out.println("SUS: " + chatItemArrayList.size());
        notifyDataSetChanged();
    }

    public void addAllAtIndex(int index, List<UtentiChat> utentiItem){
        chatItemArrayList.addAll(index, utentiItem);
        System.out.println("SUS: " + chatItemArrayList.size());
        notifyDataSetChanged();

    }
*/
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtNome;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.nomeChat);
            mainLayout = itemView.findViewById(R.id.cardListChat);
        }
    }
}
