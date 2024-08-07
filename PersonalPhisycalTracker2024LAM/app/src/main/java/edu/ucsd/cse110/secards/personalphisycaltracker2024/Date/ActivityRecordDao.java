package edu.ucsd.cse110.secards.personalphisycaltracker2024.Date;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityRecord;

@Dao
public interface ActivityRecordDao {
    @Insert
    void insert(ActivityRecord activityRecord);

    @Query("SELECT * FROM activity_records")
    List<ActivityRecord> getAllRecords();

    @Query("SELECT * FROM activity_records WHERE date = :date")
    List<ActivityRecord> getActivitiesForDate(long date);

}