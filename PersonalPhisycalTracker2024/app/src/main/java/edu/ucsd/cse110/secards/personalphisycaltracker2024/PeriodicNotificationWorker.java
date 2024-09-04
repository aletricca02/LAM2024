package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class PeriodicNotificationWorker extends Worker {

    public static final String CHANNEL_ID = "periodic_notification_channel";

    public PeriodicNotificationWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        // Logica per inviare una notifica
        NotificationUtils.showNotification(getApplicationContext(), "Ciao, Cosa fai?", "Entra e inzia a registrare le tue attività");
        return Result.success();
    }


}
