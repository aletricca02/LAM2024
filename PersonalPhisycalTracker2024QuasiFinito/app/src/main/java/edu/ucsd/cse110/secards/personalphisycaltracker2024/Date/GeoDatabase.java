package edu.ucsd.cse110.secards.personalphisycaltracker2024.Date;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo.GeofenceEntity;

@Database(entities = {GeofenceEntity.class}, version = 1, exportSchema = false)
public abstract class GeoDatabase extends RoomDatabase {
    private static volatile GeoDatabase INSTANCE;

    public abstract GeofenceDao geofenceDao();

    public static GeoDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GeoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    GeoDatabase.class, "geo_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
