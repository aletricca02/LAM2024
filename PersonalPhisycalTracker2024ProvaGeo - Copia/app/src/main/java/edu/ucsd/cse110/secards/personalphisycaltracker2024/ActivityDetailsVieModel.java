package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.Date.AppDatabase;

public class ActivityDetailsVieModel extends AndroidViewModel {

    private final AppDatabase db;
    private MutableLiveData<List<ActivityRecord>> allActivities;
    List<ActivityRecord> activities;

    public ActivityDetailsVieModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getDatabase(application);
        allActivities = new MutableLiveData<>(new ArrayList<>());
        activities = new ArrayList<>();
    }

    protected void loadActivitiesforDateAndFilter(String date, String Filter) {
        new Thread(() -> {
            activities = db.activityRecordDao().getFilteredActivities(date, Filter);
            allActivities.postValue(activities); // Notifica l'UI quando i dati sono pronti
        }).start();
    }

    public void loadActivitiesforDate(String date) {
        new Thread(() -> {
            activities = db.activityRecordDao().getActivitiesForDate(date);
            allActivities.postValue(activities); // Notifica l'UI quando i dati sono pronti
        }).start();
    }

    public void loadActivities() {
        new Thread(() -> {
            activities = db.activityRecordDao().getAllRecords();
            allActivities.postValue(activities); // Notifica l'UI quando i dati sono pronti
        }).start();
    }

    public List<ActivityRecord> getActivities(){
        return activities;
    }

    public LiveData<List<ActivityRecord>> getAllActivities() {
        return allActivities;
    }
}