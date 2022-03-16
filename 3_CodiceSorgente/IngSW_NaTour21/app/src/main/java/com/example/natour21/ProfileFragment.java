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

public class ProfileFragment extends Fragment {
//Dovrà prendere solo i post pubblicati dall utente (confronto con nome nella classe Utente)
    ArrayList<ParentItem> parentItemArrayList;
    RecyclerView RVutente;

    //Prenderli dal DB
    String[] nomi = {"xxxxxxx", "yyyyyy", "zzzz", "pppp"};
    String[] diff = {"+", "-", "=", "+"};
    String[] tempo = {"12", "22", "3", "66"};
    String[] area = {"AAAA", "BBB", "CCC", "DDDD"};
    String[] utente = {"Tizio", "Maria", "Carlo", "Bob"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @NonNull ViewGroup container, @NonNull Bundle saveInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile, container, false);
        RVutente = view.findViewById(R.id.PostUtente);

        parentItemArrayList = new ArrayList<>();

        for (int i = 0; i < nomi.length; i++) {
            ParentItem parentItem = new ParentItem(nomi[i], diff[i], tempo[i], area[i], utente[i]);
            parentItemArrayList.add(parentItem);
        }

        RVutente.setLayoutManager(new LinearLayoutManager(this.getContext()));
        RVutente.setAdapter(new MyAdapter(this.getActivity(), parentItemArrayList));
        return view;
    }
}