package edu.ucsd.cse110.secards.personalphisycaltracker2024;

//la libreria room serve per lavorare facilmente con un database SQLite
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "activity_records")
public class ActivityRecord implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    private long date;
    public String activityName;
    public long duration; // Duration in milliseconds

    public ActivityRecord(String activityName, long duration,  long date) {
        this.activityName = activityName;
        this.duration = duration;
        this.date = date;

    }

    protected ActivityRecord(Parcel in) {
        id = in.readInt();
        date = in.readLong();
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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
        dest.writeLong(date);
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