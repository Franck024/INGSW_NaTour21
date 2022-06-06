package com.example.natour21.work;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.amplifyframework.rx.RxAmplify;
import com.amplifyframework.rx.RxStorageBinding;
import com.amplifyframework.storage.result.StorageUploadInputStreamResult;
import com.example.natour21.DAOFactory.DAOFactory;
import com.example.natour21.DAOs.DAOItinerario;
import com.example.natour21.R;
import com.example.natour21.entities.Itinerario;
import com.example.natour21.exceptions.InvalidConnectionSettingsException;
import com.example.natour21.exceptions.WrappedCRUDException;
import com.example.natour21.notification.NotificationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WorkerTrackUpload extends Worker{


    private DAOItinerario DAOItinerario;

    public WorkerTrackUpload(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        if (!data.hasKeyWithValueOfType("TRACCIATO", Byte[].class)
                || !data.hasKeyWithValueOfType("ITINERARIO", String.class))
        {
            String failReason = "INVALID INPUT\n" + "TRACCIATO_isThere: " + data.hasKeyWithValueOfType("TRACCIATO", Byte[].class)
                    + "\n ITINERARIO_isThere: " + data.hasKeyWithValueOfType("ITINERARIO", String.class);
            failLog(failReason);
            return Result.failure(new Data.Builder()
                    .putString("REASON", failReason)
                    .build());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] dataTracciato = data.getByteArray("TRACCIATO");
        String dataItinerario = data.getString("ITINERARIO");
        ByteArrayInputStream inputTrack = new ByteArrayInputStream(dataTracciato);
        Integer notificationId = null;
        try {
            Itinerario itinerario = objectMapper.readValue(dataItinerario, Itinerario.class);
            DAOItinerario = DAOFactory.getDAOItinerario();
            String key = DAOItinerario.getUniqueTracciatoKey();
            itinerario.setTracciatoKey(key);
            notificationId = showUploadNotification();
            s3Upload(key, inputTrack, itinerario, notificationId);
        }
        catch (InvalidConnectionSettingsException | WrappedCRUDException | JsonProcessingException exception ){
            if (notificationId != null && (exception instanceof WrappedCRUDException))
                uploadProgressNotification(notificationId, false, dataTracciato, dataItinerario);
            {
                String failReason = exception.getMessage() + "\n" + exception.getStackTrace().toString();
                failLog(failReason);
                return Result.failure(new Data.Builder()
                        .putString("REASON", failReason)
                        .build());
            }
        }
        return Result.success();
    }

    private void s3Upload(String key, ByteArrayInputStream track, Itinerario itinerario, int notificationId){
        RxStorageBinding.RxProgressAwareSingleOperation<StorageUploadInputStreamResult> operation =
                RxAmplify.Storage.uploadInputStream(key, track);
        Callable<Boolean> insertItinerarioCallable = () ->
                DAOItinerario.insertItinerario(itinerario);
        operation.observeResult()
                .observeOn(Schedulers.io())
                .flatMap(value -> Single.fromCallable(insertItinerarioCallable))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    Log.i("TrackUploadWorker", "Itinerario caricato con successo.");
                    uploadProgressNotification(notificationId, true, null, null);
                }, error -> {
                    Log.e("TrackUploadWorker",
                            "Errore caricamento itinerario: " + error.getMessage(),
                            error);
                    byte[] tracciatoBytes = new byte[track.available()];
                    track.read(tracciatoBytes);
                    track.reset();
                    uploadProgressNotification(notificationId, false,
                            tracciatoBytes, new ObjectMapper().writeValueAsString(itinerario));
                });
    }

    private int showUploadNotification(){
        return NotificationUtils.createProgressNotification(getApplicationContext(), "NaTour",
                "Caricamento in corso...", R.drawable.upload_icon);
    }

    private void uploadProgressNotification(int notificationId, boolean isSuccessful,
                                            byte[] tracciato, String itinerario){
        Intent retryIntent = null;
        if (tracciato != null && itinerario != null && !isSuccessful){
            retryIntent = new Intent(getApplicationContext(), BroadCastReceiverTrackUpload.class);
            retryIntent.putExtra("TRACCIATO", tracciato);
            retryIntent.putExtra("ITINERARIO", itinerario);
        }
        NotificationUtils.updateProgressFinishedNotification
                (getApplicationContext(), notificationId, "NaTour",
                isSuccessful ? "Caricamento riuscito." : "Caricamento fallito",
                isSuccessful, retryIntent);
    }

    private void failLog(String failureReason){
        Log.e("WorkerTrackUpload", failureReason);
    }
}
