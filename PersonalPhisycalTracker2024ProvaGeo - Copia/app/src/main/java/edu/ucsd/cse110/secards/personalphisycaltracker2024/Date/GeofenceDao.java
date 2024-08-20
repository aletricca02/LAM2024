package edu.ucsd.cse110.secards.personalphisycaltracker2024.Date;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo.GeofenceEntity;

@Dao
public interface GeofenceDao {
    @Insert
    void insert(GeofenceEntity geofence);

    @Update
    void update(GeofenceEntity geofence);

    @Query("SELECT * FROM geofences")
    List<GeofenceEntity> getAllGeofences();

    @Query("SELECT * FROM geofences WHERE latitude = :lat AND longitude = :lon")
    GeofenceEntity getGeofenceByCoordinates(double lat ,double lon);

}