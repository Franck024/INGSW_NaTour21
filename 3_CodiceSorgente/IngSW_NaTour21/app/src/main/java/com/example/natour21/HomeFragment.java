package com.example.natour21;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    ArrayList<ParentItem> parentItemArrayList;
    RecyclerView RVparent;


    //Prenderli dal DB
    String[] nomi = {"xxxxxxx", "yyyyyy", "zzzz", "pppp"};
    String[] diff = {"+", "-", "=", "+"};
    String[] tempo = {"12", "22", "3", "66"};
    String[] area = {"AAAA", "BBB", "CCC", "DDDD"};
    String[] utente = {"Tizio", "Maria", "Carlo", "Bob"};


@Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @NonNull ViewGroup container, @NonNull Bundle saveInstanceState){
    // Carico post
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);
        RVparent = view.findViewById(R.id.RVparent);

        parentItemArrayList = new ArrayList<>();

        for(int i = 0; i< nomi.length; i++){
            ParentItem parentItem = new ParentItem(nomi[i], diff[i], tempo[i], area[i], utente[i]);
            parentItemArrayList.add(parentItem);
        }

        RVparent.setLayoutManager(new LinearLayoutManager(this.getContext()));
        RVparent.setAdapter(new MyAdapter(this.getActivity(), parentItemArrayList));

    return view;
}

}