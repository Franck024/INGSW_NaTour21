package com.example.natour21;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final Activity activity;
    ArrayList<ParentItem> parentItemArrayList;

    public MyAdapter(Activity activity, ArrayList<ParentItem> parentItemArrayList) {
        this.activity =  activity;
        this.parentItemArrayList = parentItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParentItem parentItem = parentItemArrayList.get(position);

        holder.txtNome.setText(parentItem.nomePercorso);
        holder.txtDiff.setText(parentItem.difficolta);
        holder.txtArea.setText(parentItem.area);
        holder.txtTempo.setText(parentItem.tempo);
        holder.rateRB.setRating(parentItem.rate);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
    }

    @Override
    public int getItemCount() {
        return parentItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtNome, txtDiff, txtTempo, txtArea;
        RatingBar rateRB;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            txtNome = itemView.findViewById(R.id.NomePost);
            txtDiff = itemView.findViewById(R.id.txtViewRisultatoDifficolta);
            txtTempo = itemView.findViewById(R.id.txtViewRisultatoArea);
            txtArea = itemView.findViewById(R.id.txtViewRisultatoTempo);
            rateRB = itemView.findViewById(R.id.rtbHighScore);

        }
    }
}