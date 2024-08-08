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
    public long duration; // Duration in milliseconds
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ActivityRecord(String activityName, long duration,  String date) {
        this.activityName = activityName;
        this.duration = duration;
        this.date = date;

    }

    protected ActivityRecord(Parcel in) {
        id = in.readInt();
        date = in.readString();
        activityName = in.readString();
        duration = in.readLong();
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

    public String getActivityName(){
        return activityName;
    }

    public String getDate() {
        return date;
    }


    public long getDuration(){
        return duration;
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
    }

    @Override
    public String toString() {
        return "ActivityRecord{" +
                "date=" + date +
                ", activityName='" + activityName + '\'' +
                ", activityduration='" + duration+ '\'' +
                '}';
    }
}