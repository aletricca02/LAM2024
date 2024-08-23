package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityRecord;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.Date.GeoDatabase;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo.GeofenceEntity;

public class GeoViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> isInsideGeofence;
    private final MutableLiveData<List<GeofenceEntity>> geos;
    private final GeoDatabase db;
    private List<GeofenceEntity> allgeofences;
    private boolean isAdd = false;

    // Costruttore
    public GeoViewModel(@NonNull Application application) {
        super(application);
        db = GeoDatabase.getInstance(application); // Ottieni l'istanza del database
        geos = new MutableLiveData<>();
        allgeofences = new ArrayList<>();
        isInsideGeofence = new MutableLiveData<>(false);
        loadActivities(); // Carica le attività inizialmente
    }


    public LiveData<List<GeofenceEntity>> getGeofences() {
        return geos;
    }

    private void loadActivities() {
        new Thread(() -> {
            // Recupera tutte le geofence dal database
            allgeofences = db.geofenceDao().getAllGeofences();
            geos.postValue(allgeofences); // Aggiorna il LiveData
        }).start();
    }


    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raggio della Terra in chilometri
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (R * c)/1000; // Distanza in chilometri
    }

    public boolean isInside(double geofenceLat, double geofenceLng, double radius){
        boolean b = false;
        allgeofences = db.geofenceDao().getAllGeofences();
        for(int i = 0; i<allgeofences.size() && !b; i++){
            double distance = haversine(geofenceLat, geofenceLng, allgeofences.get(i).latitude, allgeofences.get(i).longitude);
            if (distance <= radius) {
                b = true;
            }
        }

        return b;
    }

    public void addGeofence(double latitude, double longitude, float radius) {
        new Thread(() -> {
            // Controlla se esiste già una geofence con la stessa latitudine e longitudine
            //GeofenceEntity existingGeofence = db.geofenceDao().getGeofenceByCoordinates(latitude, longitude);
            if (!isInside(latitude, longitude, radius)) {
                GeofenceEntity geofenceEntity = new GeofenceEntity(latitude, longitude, radius, true);

                db.geofenceDao().insert(geofenceEntity);

                // Ricarica le geofence dal database e aggiorna il LiveData
                allgeofences = db.geofenceDao().getAllGeofences();
                isAdd = true;
                geos.postValue(allgeofences);
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplication(), "Geofence registrata!", Toast.LENGTH_SHORT).show()
                );
            } else {

                // Posta il Toast sul thread principale
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplication(), "Sei già in una geofence!", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    public void removeGeofence(GeofenceEntity geofence) {
        new Thread(() -> {
            // Rimuovi la geofence dal database
            db.geofenceDao().delete(geofence);

            // Ricarica tutte le geofence dal database e aggiorna il LiveData
            allgeofences = db.geofenceDao().getAllGeofences();
            geos.postValue(allgeofences);

            // Mostra un Toast per confermare la rimozione, deve essere eseguito nel thread principale
          /*  new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(getApplication(), "Geofence rimossa!", Toast.LENGTH_SHORT).show()
            );*/
        }).start();
    }

    public  List<GeofenceEntity> getGeos(){
        return db.geofenceDao().getAllGeofences();
    }

    public void setIsInsideGeofence(boolean inside) {
        isInsideGeofence.postValue(inside);
    }
}