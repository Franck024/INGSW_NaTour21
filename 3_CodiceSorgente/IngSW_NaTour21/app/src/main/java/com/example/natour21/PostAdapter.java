package com.example.natour21;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.controller.Controller_itinerario;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    ArrayList<ParentItem> parentItemArrayList;
    Context context;

    public PostAdapter(Context ct, ArrayList<ParentItem> parentItemArrayList){
        context =  ct;
        this.parentItemArrayList = parentItemArrayList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        ParentItem parentItem = parentItemArrayList.get(position);

        holder.txtNome.setText(parentItem.getNomeItinerario());
        holder.txtDiff.setText(parentItem.getDifficolta());
        holder.txtArea.setText(parentItem.getNomePuntoIniziale());
        holder.txtTempo.setText(parentItem.getDurata());
        holder.txtNomeUtente.setText(parentItem.getAutore());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Controller_itinerario.class);
                Log.i("VIEWHOLDERITIN", parentItem.getIdItinerario() + "");
                intent.putExtra("ITINERARIO_ID", parentItem.getIdItinerario());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parentItemArrayList.size();
    }

    public void addAll(List<ParentItem> parentItems){
        parentItemArrayList.addAll(parentItems);
        notifyDataSetChanged();
    }

    public void addAllAtIndex(int index, List<ParentItem> parentItems){
        parentItemArrayList.addAll(index, parentItems);
        notifyDataSetChanged();

    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        TextView txtNome, txtDiff, txtTempo, txtArea, txtNomeUtente;
        CardView cardView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.NomePost);
            txtDiff = itemView.findViewById(R.id.txtViewRisultatoDifficolta);
            txtTempo = itemView.findViewById(R.id.txtViewRisultatoTempo);
            txtArea = itemView.findViewById(R.id.txtViewRisultatoArea);
            txtNomeUtente = itemView.findViewById(R.id.txtUtentePost);
            cardView = itemView.findViewById(R.id.CardView);
        }
    }
}
