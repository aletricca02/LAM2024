package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.NotificationWorker;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        Log.d("GeofenceReceiver", geofencingEvent.toString());
        if (geofencingEvent.hasError() || geofencingEvent == null) {
            Log.d("GeofenceReceiver", "Error in geofencing event: " + geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL || geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d("GeofenceReceiver", "Geofence entered!");

            // Avvia il lavoro di notifica
            Data inputData = new Data.Builder()
                    .putString(NotificationWorker.INPUT_KEY_NOTIFICATION_TYPE, NotificationWorker.NOTIFICATION_TYPE_GEOFENCE)
                    .build();

            OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInputData(inputData)
                    .build();

            WorkManager.getInstance(context).enqueue(notificationWork);
        }else{
            Log.d("GeofenceReceiver", "Uscito dalla Geofence");
        }
    }
}