package com.example.natour21.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class BroadCastReceiverTrackUpload extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Non dovrebbe mai accadere
        if (!intent.hasExtra("ITINERARIO") || !intent.hasExtra("TRACCIATO")) return;
        String intentItinerario = intent.getStringExtra("ITINERARIO");
        byte[] intentTracciato = intent.getByteArrayExtra("TRACCIATO");
        if (intentTracciato == null || intentItinerario == null) return;
        WorkManager workManager = WorkManager.getInstance(context);
        Data data = new Data.Builder()
                .putString("ITINERARIO", intentItinerario)
                .putByteArray("TRACCIATO", intentTracciato)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(WorkerTrackUpload.class)
                .setInputData(data)
                .setConstraints(constraints)
                .build();
        goAsync();
        workManager.enqueue(uploadWorkRequest);
    }
}
