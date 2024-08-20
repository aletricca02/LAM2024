package edu.ucsd.cse110.secards.personalphisycaltracker2024;

//la libreria room serve per lavorare facilmente con un database SQLite
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Entity(tableName = "activity_records")
public class ActivityRecord implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    private String date; // Salviamo la data come String
    public String activityName;
    public int steps;
    public long duration; // Duration in milliseconds

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Costruttore senza argomenti
    public ActivityRecord() {
    }

    public ActivityRecord(String activityName, long duration, String date, Integer steps) {
        this.activityName = activityName;
        this.duration = duration;
        this.date = date;
        this.steps = steps;
    }

    // Costruttore per Parcel
    protected ActivityRecord(Parcel in) {
        id = in.readInt();
        date = in.readString();
        activityName = in.readString();
        duration = in.readLong();
        steps = in.readInt();
    }

    public static final Creator<ActivityRecord> CREATOR = new Creator<ActivityRecord>() {
        @Override
        public ActivityRecord createFromParcel(Parcel in) {
            return new ActivityRecord(in);
        }

        @Override
        public ActivityRecord[] newArray(int size) {
            return new ActivityRecord[size];
        }
    };

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getSteps() {
        return "Passi effettuati: "+steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(date);
        dest.writeString(activityName);
        dest.writeLong(duration);
        dest.writeInt(steps);
    }

    @Override
    public String toString() {
        return "ActivityRecord{" +
                "date='" + date + '\'' +
                ", activityName='" + activityName + '\'' +
                ", duration=" + duration +
                ", steps=" + steps +
                '}';
    }
}