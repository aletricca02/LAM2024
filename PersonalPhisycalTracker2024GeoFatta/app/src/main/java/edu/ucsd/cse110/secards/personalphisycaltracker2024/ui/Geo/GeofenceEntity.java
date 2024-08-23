package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "geofences")
public class GeofenceEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double latitude;
    public double longitude;
    public float radius;
    public boolean isInside; // true se l'utente è all'interno della geofence, false altrimenti

    public GeofenceEntity(double latitude, double longitude, float radius, boolean isInside) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.isInside = isInside;
    }

    public String getId() {
        return id+"";
    }

    public double getLatitude() {
        return latitude;
    }

    public float getRadius() {
        return radius;
    }

    public double getLongitude() {
        return longitude;
    }
}