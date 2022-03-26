package com.example.natour21.controller;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.natour21.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Controller_Statistiche extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_statistiche);

        BarChart barchart = findViewById(R.id.barChart);

        ArrayList<BarEntry> visitors = new ArrayList<>();
        /// estrarre valori dal db e inserirli sull'asse y
        visitors.add(new BarEntry(1, 10));
        visitors.add(new BarEntry(2, 15));
        visitors.add(new BarEntry(3, 27));
        visitors.add(new BarEntry(4, 9));
        visitors.add(new BarEntry(5, 0));
        visitors.add(new BarEntry(6, 0));

        BarDataSet barDataSet = new BarDataSet(visitors, "Conteggio");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barchart.setFitBars(true);
        barchart.setData(barData);
        barchart.getDescription().setText("Statistiche");
        barchart.animateY(2000);


    }
}
