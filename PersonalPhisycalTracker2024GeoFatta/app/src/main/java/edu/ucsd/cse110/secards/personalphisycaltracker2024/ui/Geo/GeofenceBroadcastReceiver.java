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

import edu.ucsd.cse110.secards.personalphisycaltracker2024.GeofenceNotificationWorker;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.PeriodicNotificationWorker;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        Log.d("Geofence", "Ho ricevuto qualcosa");

        if (geofencingEvent == null|| geofencingEvent.hasError()) {
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.d("GeofenceReceiver", geofenceTransition+"");

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Utente è entrato nella geofence
            // Avvia il WorkManager per le notifiche periodiche
            Log.d("Geofence", "si");

            PeriodicNotificationWorker.startPeriodicNotifications(context);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Utente è uscito dalla geofence
            Log.d("Geofence", "no");

            // Ferma le notifiche periodiche
            PeriodicNotificationWorker.stopPeriodicNotifications(context);
        }
    }
}