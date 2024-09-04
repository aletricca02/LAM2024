package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "activity_stats")
public class ActivityStats {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "activityName")
    public String activity_type;

    @ColumnInfo(name = "total_duration")
    public long total_duration;

    // Costruttore senza argomenti richiesto da Room
    public ActivityStats() {
    }

    public ActivityStats(String activity_type, long total_duration) {
        this.activity_type = activity_type;
        this.total_duration = total_duration;
    }

    // Getter e Setter
    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public long getTotal_duration() {
        return total_duration;
    }

    public void setTotal_duration(long total_duration) {
        this.total_duration = total_duration;
    }
}