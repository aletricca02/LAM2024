package edu.ucsd.cse110.secards.personalphisycaltracker2024.Date;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityRecord;

@Database(entities = {ActivityRecord.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ActivityRecordDao activityRecordDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "activity_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}