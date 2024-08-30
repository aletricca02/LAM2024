package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.home.HomeFragment;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("Marco", "ho ricevuto qualcosa");
        //Toast.makeText(context,"Geofence triggered",Toast.LENGTH_LONG).show();
        NotificationHelper notificationHelper = new NotificationHelper(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()) {
            Log.d("Marco","Ricevuto errore");
            return;
        }
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                //Toast.makeText(context,"enter",Toast.LENGTH_LONG).show();
                notificationHelper.sendHighPriorityNotification("Sei dentro una Geofence!","Entra e inizia a registrare la tua attività", HomeFragment.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                notificationHelper.sendHighPriorityNotification("Sei in una Geofence!","Entra e inizia a registrare la tua attività", HomeFragment.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                notificationHelper.sendHighPriorityNotification("Sei uscito da una Geofence!","Che peccato...", HomeFragment.class);
                break;

        }
    }
}