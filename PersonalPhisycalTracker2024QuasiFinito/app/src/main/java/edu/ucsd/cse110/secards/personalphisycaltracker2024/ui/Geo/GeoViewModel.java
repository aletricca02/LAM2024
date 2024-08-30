package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo;

import android.app.Application;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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


    public int androidLocationDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return (int)results[0]; // Distanza in metri
    }

    public boolean isInside(double geofenceLat, double geofenceLng, double radius) {
        boolean isInside = false;
        for (GeofenceEntity geofence : allgeofences) {
            double distance2 = androidLocationDistance(geofenceLat,  geofenceLng, geofence.latitude, geofence.longitude);
            Log.d("prova distanze",distance2+"");
            if (distance2 <= radius) { // Converti il raggio in chilometri
                isInside = true;
                break;
            }
        }
        return isInside;
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
            db.geofenceDao().delete(geofence);

            // Ricarica tutte le geofence dal database e aggiorna il LiveData
            allgeofences = db.geofenceDao().getAllGeofences();
            geos.postValue(allgeofences);

        }).start();
    }

    public  List<GeofenceEntity> getGeos(){
        return db.geofenceDao().getAllGeofences();
    }

    public void setIsInsideGeofence(boolean inside) {
        isInsideGeofence.postValue(inside);
    }
}