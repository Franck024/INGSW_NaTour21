package com.example.natour21.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.natour21.R;

public class NotificationUtils {

    private static final String CHANNEL_ID = "NATOURNOTIFCHANNEL";
    private static final String CHANNEL_NAME ="NaTour";
    private static final String CHANNEL_DESCRIPTION = "Notifiche di NaTour.";

    private static int uniqueNotifId = 0;

    public static void createNotificationChannel(Application application) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = application.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static int createProgressNotification
            (Context context, String title, String description, int iconResourceId){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(iconResourceId)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(0, 0, true);

        int notificationId = getUniqueNotifId();
        notificationManager.notify(notificationId, builder.build());
        return notificationId;
    }


    public static void updateProgressFinishedNotification(Context context, int notificationId,
                                                          String title, String description,
                                                          boolean isSuccessful, Intent retryIntent){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(isSuccessful ? R.drawable.success_icon : R.drawable.failure_icon)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setProgress(0, 0, false)
                .setAutoCancel(true);

        if (!isSuccessful && retryIntent != null) {
            PendingIntent pendingIntent = PendingIntent
                    .getBroadcast(context, 0, retryIntent, 0);
            builder.addAction(0, "Riprova", pendingIntent);
        }

        notificationManager.notify(notificationId, builder.build());
    }


    private static int getUniqueNotifId(){
        return uniqueNotifId++;
    }





}
