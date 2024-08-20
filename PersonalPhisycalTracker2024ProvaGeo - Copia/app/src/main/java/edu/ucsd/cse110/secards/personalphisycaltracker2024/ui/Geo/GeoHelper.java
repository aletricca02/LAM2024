package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

public class GeoHelper extends ContextWrapper {
    private PendingIntent pendingIntent;
    private GeofencingClient geofencingClient;

    public GeoHelper(Context base) {
        super(base);
        geofencingClient = LocationServices.getGeofencingClient(this);

    }

    public Geofence createGeofence(String geofenceId, double lat, double lng, float radius) {
        // Imposta un ritardo di permanenza (dwell time) in millisecondi
        int loiteringDelay = 10000; // 10 secondi (puoi scegliere un valore appropriato)

        Geofence.Builder builder = new Geofence.Builder()
                .setRequestId(geofenceId)
                .setCircularRegion(lat, lng, radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setExpirationDuration(Geofence.NEVER_EXPIRE);

        // Imposta il ritardo di permanenza se il tipo di transizione include GEOFENCE_TRANSITION_DWELL
        if ((Geofence.GEOFENCE_TRANSITION_DWELL) != 0) {
            builder.setLoiteringDelay(loiteringDelay);
        }

        return builder.build();
    }

    public GeofencingRequest createGeofencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .addGeofence(geofence)
                .build();
    }

    public PendingIntent getPendingIntent() {
        Intent intent = new Intent("com.google.android.gms.location.geofence.action.GEOFENCE_TRANSITION");
        intent.setClass(this, GeofenceBroadcastReceiver.class);
        Log.d("intent", intent.toString());
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}