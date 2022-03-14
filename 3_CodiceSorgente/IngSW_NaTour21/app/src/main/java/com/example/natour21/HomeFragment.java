package com.example.natour21;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    //Prova impl frame Home
    MyAdapter myAdapter;
    ArrayList<ParentItem> parentItemArrayList;
    RecyclerView RVparent;

    //Fine prova

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //PROVA

      View view = inflater.inflate(R.layout.fragment_home, container, false);
      RVparent = (RecyclerView) view.findViewById(R.id.RVparent);

        //Prenderli dal DB
        String[] nomi = {"xxxxxxx", "yyyyyy", "zzzz", "pppp"};
        String[] diff = {"+", "-", "=", "+"};
        String[] tempo = {"12", "22", "3", "66"};
        String[] area = {"AAAA", "BBB", "CCC", "DDDD"};
        Float[] rate = {1f,2f,3f,4f};
        parentItemArrayList = new ArrayList<>();

        for(int i = 0; i< nomi.length; i++){
            ParentItem parentItem = new ParentItem(nomi[i], diff[i], tempo[i], area[i], rate[i]);
            parentItemArrayList.add(parentItem);
        }
        myAdapter = new MyAdapter(this.getActivity(), parentItemArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        RVparent.setLayoutManager(linearLayoutManager);
        RVparent.setAdapter(myAdapter);
        // FINE
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}