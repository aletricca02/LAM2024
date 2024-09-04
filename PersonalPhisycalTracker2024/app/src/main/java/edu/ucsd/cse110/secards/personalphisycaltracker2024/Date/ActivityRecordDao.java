package edu.ucsd.cse110.secards.personalphisycaltracker2024.Date;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityRecord;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityStats;

@Dao
public interface ActivityRecordDao {
    @Insert
    void insert(ActivityRecord activityRecord);

    @Query("SELECT * FROM activity_records")
    List<ActivityRecord> getAllRecords();

    @Query("SELECT * FROM activity_records WHERE date = :date")
    List<ActivityRecord> getActivitiesForDate(String date);

    @Query("SELECT * FROM activity_records WHERE date = :date AND activityName = :activityType")
    List<ActivityRecord> getFilteredActivities(String date, String activityType);

    @Query("SELECT id, activityName,SUM(duration) as total_duration FROM activity_records WHERE strftime('%Y-%m', date) = :month GROUP BY activityName")
    List<ActivityStats> getActivitiesForMonth(String month);

    @Query("SELECT id, activityName,SUM(duration) as total_duration FROM activity_records WHERE  date = :day GROUP BY activityName")
    List<ActivityStats> getActivitiesForDay(String day);


}