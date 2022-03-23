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

import java.util.ArrayList;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {
    ArrayList<ParentItem> parentItemArrayList;
    Context context;

    public MyAdapter2(Context ct, ArrayList<ParentItem> parentItemArrayList){
        context =  ct;
        this.parentItemArrayList = parentItemArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ParentItem parentItem = parentItemArrayList.get(position);

        holder.txtNome.setText(parentItem.nomePercorso);
        holder.txtDiff.setText(parentItem.difficolta);
        holder.txtArea.setText(parentItem.area);
        holder.txtTempo.setText(parentItem.tempo);
        holder.txtNomeUtente.setText(parentItem.utente);
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Controller_itinerario.class);
               // Intent putExtra 5.22 https://youtu.be/xgpLYwEmlO0
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parentItemArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtNome, txtDiff, txtTempo, txtArea, txtNomeUtente;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.NomePost);
            txtDiff = itemView.findViewById(R.id.txtViewRisultatoDifficolta);
            txtTempo = itemView.findViewById(R.id.txtViewRisultatoArea);
            txtArea = itemView.findViewById(R.id.txtViewRisultatoTempo);
            txtNomeUtente = itemView.findViewById(R.id.txtUtentePost);
            mainLayout = itemView.findViewById(R.id.CardView);
        }
    }
}
