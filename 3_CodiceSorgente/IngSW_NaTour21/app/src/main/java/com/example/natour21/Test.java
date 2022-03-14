package com.example.natour21;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Test extends AppCompatActivity {
    MyAdapter myAdapter;
    ArrayList<ParentItem> parentItemArrayList;
    RecyclerView RVparent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        //PROVA

        RVparent = findViewById(R.id.RVparent);
        //Prenderli dal DB
        String[] nomi = {"xxxxxxx", "yyyyyy"};
        String[] diff = {"+", "-"};
        String[] tempo = {"12", "22"};
        String[] area = {"AAAA", "BBB"};
        Float[] rate = {1f,2f};
        parentItemArrayList = new ArrayList<>();

        for(int i = 0; i< nomi.length; i++){
            ParentItem parentItem = new ParentItem(nomi[i], diff[i], tempo[i], area[i], rate[i]);
            parentItemArrayList.add(parentItem);
        }
        myAdapter = new MyAdapter(this, parentItemArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        RVparent.setLayoutManager(linearLayoutManager);
        RVparent.setAdapter(myAdapter);
        // FINE

    }
}
