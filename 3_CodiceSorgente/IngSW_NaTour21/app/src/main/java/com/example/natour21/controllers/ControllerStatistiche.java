package com.example.natour21.controllers;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOStatistiche;
import com.example.natour21.R;
import com.example.natour21.entities.Statistiche;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ControllerStatistiche extends AppCompatActivity {


    private enum StatisticheColumnNames{
        UTENTI,
        ACCESSI,
        ITINERARI,
        CHAT,
        MESSAGGI;

        @Override
        public String toString(){
                if (this.equals(UTENTI)) return "Utenti";
                if (this.equals(ACCESSI)) return "Accessi";
                if (this.equals(ITINERARI)) return "Itinerari";
                if (this.equals(CHAT)) return "Chat";
                if (this.equals(MESSAGGI)) return "Messaggi";
                return "INVALID ENUM";
        }
    }

    private class XAxisValueFormatter extends ValueFormatter {

        StatisticheColumnNames[] values = StatisticheColumnNames.values();

        @Override
        public String getFormattedValue(float value) {
            int intValueString = Integer.valueOf((int) value);
            if (intValueString >= values.length) return "INVALID";
            return values[intValueString].toString();
        }

    }
    private DAOStatistiche DAOStatistiche;
    private BarEntry barEntryUtenti, barEntryAccessi, barEntryItinerari, barEntryChat, barEntryMessaggi;
    private ArrayList<BarEntry> barEntriesStatistiche = new ArrayList<>();
    private BarChart barChart;
    private ImageButton btnBack;
    private final int GRAPH_Y_ANIMATION_DURATION_MS = 2000;
    private final int STATISTICHE_REFRESH_FREQUENCY_MS = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistiche);

        barChart = findViewById(R.id.barChart);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        barEntryUtenti = new BarEntry(StatisticheColumnNames.UTENTI.ordinal(), 0);
        barEntryAccessi  = new BarEntry(StatisticheColumnNames.ACCESSI.ordinal(), 0);
        barEntryItinerari = new BarEntry(StatisticheColumnNames.ITINERARI.ordinal(), 0);
        barEntryChat = new BarEntry(StatisticheColumnNames.CHAT.ordinal(), 0);
        barEntryMessaggi = new BarEntry(StatisticheColumnNames.MESSAGGI.ordinal(), 0);

        try{
            DAOStatistiche = DAOFactory.getDAOStatistiche();
        }
        catch (InvalidConnectionSettingsException icse){
            finish();
        }

        barEntriesStatistiche.add(barEntryUtenti);
        barEntriesStatistiche.add(barEntryAccessi);
        barEntriesStatistiche.add(barEntryItinerari);
        barEntriesStatistiche.add(barEntryChat);
        barEntriesStatistiche.add(barEntryMessaggi);

        barChart.setFitBars(true);
        barChart.getDescription().setText("Statistiche");
        barChart.getXAxis().setValueFormatter(new XAxisValueFormatter());


        Callable<Statistiche> getStatisticheCallable = () -> {
            return DAOStatistiche.getStatistiche();
        };

        Observable.interval(STATISTICHE_REFRESH_FREQUENCY_MS, TimeUnit.MILLISECONDS)
                .flatMap(longValue -> Observable.fromCallable(getStatisticheCallable))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(statistiche -> onNewStatisticheReceived(statistiche),
                        error -> ControllerUtils.showUserFriendlyErrorMessageAndLogThrowable
                                (getApplicationContext(),"Settings", "Impossibile caricare le statistiche.", error));
    }

    private void onNewStatisticheReceived(Statistiche statistiche){
        barEntryUtenti.setY(statistiche.getUtenteCount());
        barEntryAccessi.setY(statistiche.getUtenteAccessCount());
        barEntryItinerari.setY(statistiche.getItinerarioCount());
        barEntryChat.setY(statistiche.getChatCount());
        barEntryMessaggi.setY(statistiche.getMessaggioCount());

        BarDataSet barDataSet = new BarDataSet(barEntriesStatistiche, "Statistiche");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

}
