package edu.ucsd.cse110.secards.personalphisycaltracker2024.Date;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityRecord;

@Database(entities = {ActivityRecord.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ActivityRecordDao activityRecordDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "activity_records")
                            .addMigrations(MIGRATION_2_3) // Aggiungi la migrazione
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Crea una nuova tabella con il tipo di colonna aggiornato
            database.execSQL("CREATE TABLE IF NOT EXISTS `activity_records_new` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`date` TEXT, " +  // TEXT è il tipo per le stringhe in SQLite
                    "`activityName` TEXT, " +
                    "`duration` INTEGER NOT NULL)");

            // Copia i dati dalla vecchia tabella alla nuova
            database.execSQL("INSERT INTO `activity_records_new` (`id`, `date`, `activityName`, `duration`) " +
                    "SELECT `id`, `date`, `activityName`, `duration` FROM `activity_records`");

            // Elimina la vecchia tabella
            database.execSQL("DROP TABLE `activity_records`");

            // Rinomina la nuova tabella per sostituire la vecchia
            database.execSQL("ALTER TABLE `activity_records_new` RENAME TO `activity_records`");
        }
    };
}