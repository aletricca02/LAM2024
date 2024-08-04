package edu.ucsd.cse110.secards.personalphisycaltracker2024;

//la libreria room serve per lavorare facilmente con un database SQLite
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activity_records")
public class ActivityRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String activityName;
    public long duration; // Duration in milliseconds

    public ActivityRecord(String activityName, long duration) {
        this.activityName = activityName;
        this.duration = duration;
    }
}