package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.GeofenceBroadcastReceiver;

public class GeoHelper extends ContextWrapper {
    PendingIntent pendingIntent;
    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList = new ArrayList<>();
    private static final String TAG="GeofenceHelper";

    public GeoHelper(Context base) {
        super(base);
        geofencingClient = LocationServices.getGeofencingClient(this);

    }

    public String getErrorString(Exception e){
        return e.toString();
    }

    public Geofence createGeofence(String geofenceId, double lat, double lng, float radius) {
        // Imposta un ritardo di permanenza (dwell time) in millisecondi
        int loiteringDelay = 5000;

        return new Geofence.Builder()
                .setRequestId(geofenceId)
                .setCircularRegion(lat, lng, radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER| Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(loiteringDelay)
                .build();
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence){
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }
    public PendingIntent getPendingIntent(){
        if(pendingIntent!= null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,2607,intent,PendingIntent.FLAG_MUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }


}