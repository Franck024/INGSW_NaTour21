package com.example.natour21;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

// DA ELIMINARE ----> CLASSE SOSTITUITA

public class ItinerarioFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @NonNull ViewGroup container, @NonNull Bundle saveInstanceState) {
        // Carico post
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_itinerario, container, false);

        return view;
        ///
    }
}
