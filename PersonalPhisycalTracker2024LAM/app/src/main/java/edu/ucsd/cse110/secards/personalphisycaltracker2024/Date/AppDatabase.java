package edu.ucsd.cse110.secards.personalphisycaltracker2024.Date;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityRecord;

@Database(entities = {ActivityRecord.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ActivityRecordDao activityRecordDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "activity_records")
                            .addMigrations(MIGRATION_1_2) // Aggiungi la migrazione
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Definisci la migrazione da versione 1 a 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Aggiungi la nuova colonna alla tabella
            database.execSQL("ALTER TABLE activity_records ADD COLUMN newColumn INTEGER");
        }
    };
}