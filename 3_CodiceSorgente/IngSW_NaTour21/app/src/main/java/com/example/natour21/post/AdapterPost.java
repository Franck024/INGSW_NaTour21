package com.example.natour21.post;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.R;
import com.example.natour21.controllers.ControllerItinerarioDetails;
import com.example.natour21.controllers.ControllerProfile;
import com.example.natour21.map.MapSnapshotUtil;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.drawing.MapSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.PostViewHolder> {
    public interface OnPostClickListener {public void onClick(Post post);}

    private OnPostClickListener onPostClickListener;
    private ArrayList<Post> postArrayList;


    public AdapterPost(ArrayList<Post> postArrayList){
        this.postArrayList = postArrayList;
    }

    public AdapterPost(ArrayList<Post> postArrayList, OnPostClickListener onPostClickListener){
        this.postArrayList = postArrayList;
        this.onPostClickListener = onPostClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postArrayList.get(position);
        holder.textViewNome.setText(post.getNomeItinerario());
        holder.textViewDifficolta.setText(post.getDifficolta());
        holder.textViewArea.setText(post.getNomePuntoIniziale());
        holder.textViewTempo.setText(post.getDurata());
        holder.textViewNomeUtente.setText(post.getAutore());
        Context context = holder.cardView.getContext();
        holder.textViewNome.setOnClickListener(v -> {
            Intent intent = new Intent(context, ControllerProfile.class);
            intent.putExtra("USER_ID", post.getAutore());
            context.startActivity(intent);
        });
        holder.btnVisualizzaItinerario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPostClickListener != null) onPostClickListener.onClick(post);
                Intent intent = new Intent(context, ControllerItinerarioDetails.class);
                intent.putExtra("ITINERARIO_ID", post.getIdItinerario());
                context.startActivity(intent);
            }
        });

        if (post.getPuntoInizialeLat() == null || post.getPuntoInizialeLong() == null){
            new Handler(Looper.getMainLooper()).post(() -> { holder.progressBar.setVisibility(View.GONE);});
            return;
        }
        GeoPoint puntoIniziale = new GeoPoint(post.getPuntoInizialeLat(), post.getPuntoInizialeLong());
        MapSnapshot.MapSnapshotable mapSnapshotable = mapSnapshot -> {
            if (mapSnapshot.getStatus() != MapSnapshot.Status.CANVAS_OK) return;
            Bitmap bitmap =  Bitmap.createBitmap(mapSnapshot.getBitmap());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    //replace ProgressBar with
                    ImageView imageView = new ImageView(context);
                    imageView.setImageBitmap(bitmap);
                    ProgressBar progressBar = holder.progressBar;
                    progressBar.setVisibility(View.GONE);
                    imageView.setId(progressBar.getId());
                    imageView.setLayoutParams(progressBar.getLayoutParams());
                    holder.constraintLayoutPost.addView(imageView);
                }
            });
        };
        holder.progressBar.post( () -> MapSnapshotUtil.takeMapSnapshot(context.getApplicationContext(), puntoIniziale, mapSnapshotable,
                holder.progressBar.getWidth(), holder.progressBar.getHeight()));
    }

    @Override
    public int getItemCount() {
        return postArrayList == null ? 0 : postArrayList.size();
    }

    public void addAll(List<Post> posts){
        postArrayList.addAll(posts);
        notifyDataSetChanged();
    }

    public void addAllAtIndex(int index, List<Post> posts){
        postArrayList.addAll(index, posts);
        notifyDataSetChanged();

    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;
        TextView textViewNome, textViewDifficolta, textViewTempo, textViewArea, textViewNomeUtente;
        CardView cardView;
        Button btnVisualizzaItinerario;
        ConstraintLayout constraintLayoutPost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            textViewNome = itemView.findViewById(R.id.textViewNome);
            textViewDifficolta = itemView.findViewById(R.id.txtViewRisultatoDifficolta);
            textViewTempo = itemView.findViewById(R.id.txtViewRisultatoTempo);
            textViewArea = itemView.findViewById(R.id.txtViewRisultatoArea);
            textViewNomeUtente = itemView.findViewById(R.id.txtUtentePost);
            cardView = itemView.findViewById(R.id.CardView);
            btnVisualizzaItinerario = itemView.findViewById(R.id.btnVisualizzaItinerario);
            constraintLayoutPost = itemView.findViewById(R.id.constraintLayoutPost);
        }
    }
}
