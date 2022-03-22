package com.example.natour21;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder>{
    private final Activity activity;
    ArrayList<UtentiChat> userItemArrayList;

    public AdapterChat(Activity activity, ArrayList<UtentiChat> userItemArrayList) {
        this.activity =  activity;
        this.userItemArrayList = userItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chat,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChat.ViewHolder holder, int position) {
        UtentiChat userItem = userItemArrayList.get(position);

        holder.txtNome.setText(userItem.nome);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
    }

    @Override
    public int getItemCount() {
        return userItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtNome;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            txtNome = itemView.findViewById(R.id.nomeChat);
        }
    }
}
