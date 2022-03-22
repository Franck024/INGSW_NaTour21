package com.example.natour21;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MessageFragment extends Fragment {

    ArrayList<UtentiChat> ListChat;
    RecyclerView RVchat;


    //Prenderli dal DB
    String[] nomi = {"xxxxxxx", "yyyyyy", "zzzz", "pppp"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @NonNull ViewGroup container, @NonNull Bundle saveInstanceState) {
        // Carico post
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_message, container, false);
        RVchat = view.findViewById(R.id.RVchat);

        ListChat = new ArrayList<>();

        for (int i = 0; i < nomi.length; i++) {
            UtentiChat chatItem = new UtentiChat(nomi[i]);
            ListChat.add(chatItem);
        }

        RVchat.setLayoutManager(new LinearLayoutManager(this.getContext()));
        RVchat.setAdapter(new AdapterChat(this.getActivity(), ListChat));
        return view;
        ///
    }
}