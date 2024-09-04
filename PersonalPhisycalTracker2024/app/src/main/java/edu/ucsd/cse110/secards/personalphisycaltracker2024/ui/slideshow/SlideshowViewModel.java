package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;


import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityRecord;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityStats;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.Date.AppDatabase;

public class SlideshowViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private MutableLiveData<List<ActivityStats>> activities;
    private MutableLiveData<List<ActivityStats>> activitiesDay;
    private List<ActivityStats> prova;

    public SlideshowViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getDatabase(application);
        activities = new MutableLiveData<>(new ArrayList<>());
        activitiesDay = new MutableLiveData<>(new ArrayList<>());
        prova = new ArrayList<>();

    }
   protected void loadActivitiesforMonth(String month) {
        new Thread(() -> {
            List<ActivityStats> activitiesMonth = db.activityRecordDao().getActivitiesForMonth(month);
            activities.postValue(activitiesMonth); // Notifica l'UI quando i dati sono pronti
        }).start();
    }

    protected void loadActivitiesforDay(String day) {
        new Thread(() -> {
            List<ActivityStats> activitiesDays = db.activityRecordDao().getActivitiesForDay(day);
            activitiesDay.postValue(activitiesDays );
        }).start();
    }
    public LiveData<List<ActivityStats>> getAllActivitiesForDay() {
        return activitiesDay;
    }


    public LiveData<List<ActivityStats>> getAllActivities() {
        return activities;
    }
}