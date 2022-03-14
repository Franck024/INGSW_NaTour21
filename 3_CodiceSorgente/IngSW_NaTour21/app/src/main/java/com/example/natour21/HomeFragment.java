package com.example.natour21;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    //Prova impl frame Home
    MyAdapter myAdapter;
    ArrayList<ParentItem> parentItemArrayList;
    // ArrayList<ChildItem> childItemArrayList;
    RecyclerView RVparent;


    //Fine prova

/*
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    *//**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     *//*
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
    /*     if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        */
   //PROVA forse da mettere tutto in onCreateView
        setContentView(R.layout.fragment_home);

        RVparent = findViewById(R.id.RVparent);

        //Prenderli dal DB
        String[] nomi = {"xxxxxxx", "yyyyyy", "zzzz", "pppp"};
        String[] diff = {"+", "-", "=", "+"};
        String[] tempo = {"12", "22", "3", "66"};
        String[] area = {"AAAA", "BBB", "CCC", "DDDD"};
        Integer[] rate = {1,2,3,4};
        parentItemArrayList = new ArrayList<>();
        // childItemArrayList = new ArrayList<>();

        for(int i = 0; i< nomi.length; i++){
            ParentItem parentItem = new ParentItem(nomi[i], diff[i], tempo[i], area[i], rate[i]);
            parentItemArrayList.add(parentItem);

            // if... 23.49
        }
        myAdapter = new MyAdapter(this, parentItemArrayList); // passa anche array child
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RVparent.setLayoutManager(linearLayoutManager);
        RVparent.setAdapter(myAdapter);
    // FINE
    }
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

 */
}