package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class GeoHelper extends ContextWrapper {
    private PendingIntent pendingIntent;
    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList = new ArrayList<>();


    public GeoHelper(Context base) {
        super(base);
        geofencingClient = LocationServices.getGeofencingClient(this);

    }

    public Geofence createGeofence(String geofenceId, double lat, double lng, float radius) {
        // Imposta un ritardo di permanenza (dwell time) in millisecondi
        int loiteringDelay = 1000; // 10 secondi (puoi scegliere un valore appropriato)

        return new Geofence.Builder()
                .setRequestId(geofenceId)
                .setCircularRegion(lat, lng, radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(loiteringDelay) // Imposta il ritardo di permanenza
                .build();
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }
    public PendingIntent getPendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return pendingIntent;
    }
}