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

import edu.ucsd.cse110.secards.personalphisycaltracker2024.Date.GeoDatabase;

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



    public void addGeofence(double latitude, double longitude, float radius, Runnable onSuccess) {
        new Thread(() -> {
            // Controlla se esiste già una geofence con la stessa latitudine e longitudine
            GeofenceEntity existingGeofence = db.geofenceDao().getGeofenceByCoordinates(latitude, longitude);
            if (existingGeofence == null) {
                GeofenceEntity geofenceEntity = new GeofenceEntity(latitude, longitude, radius, true);

                db.geofenceDao().insert(geofenceEntity);

                // Ricarica le geofence dal database e aggiorna il LiveData
                allgeofences = db.geofenceDao().getAllGeofences();
                isAdd = true;
                geos.postValue(allgeofences);
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplication(), "Geofence registrata!", Toast.LENGTH_SHORT).show()
                );
                // Invoca il callback su successo
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } else {

                // Posta il Toast sul thread principale
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplication(), "Sei già in una geofence!", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }



    public void setIsInsideGeofence(boolean inside) {
        isInsideGeofence.postValue(inside);
    }
}
