package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {

    public static final String CHANNEL_ID = "notification_channel";
    public static final int NOTIFICATION_ID = 1;
    public static final String INPUT_KEY_NOTIFICATION_TYPE = "notification_type";

    public static final String NOTIFICATION_TYPE_GEOFENCE = "geofence";
    public static final String NOTIFICATION_TYPE_GENERAL = "general";

    public NotificationWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        createNotificationChannel();

        // Estrazione del parametro di input
        String notificationType = getInputData().getString(INPUT_KEY_NOTIFICATION_TYPE);
        if (notificationType == NOTIFICATION_TYPE_GENERAL) {
            sendNotification(NOTIFICATION_TYPE_GENERAL);
        }else{
            sendNotification(NOTIFICATION_TYPE_GEOFENCE);
        }

        return Result.success();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Channel";
            String descriptionText = "This is a channel for notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(descriptionText);

            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void sendNotification(String notificationType) {
        String title;
        String text;

        if (NOTIFICATION_TYPE_GEOFENCE.equals(notificationType)) {
            title = "Sei dentro una geofence!";
            text = "Inizia a registrare le tue attività.";
        } else {
            title = "What are you doing?";
            text = "Let's track your activities!";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // Assicurati che questa risorsa esista
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // Controlla se il permesso di invio notifiche è concesso
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}