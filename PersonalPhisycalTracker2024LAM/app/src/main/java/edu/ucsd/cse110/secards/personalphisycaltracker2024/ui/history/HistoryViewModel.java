package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityRecord;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.Date.AppDatabase;

/*
 ViewModel è progettato per conservare e gestire i dati dell'interfaccia
 utente in modo da sopravvivere ai cambiamenti di configurazione
 */



    public class HistoryViewModel extends AndroidViewModel {

        private final MutableLiveData<List<ActivityRecord>> activities;
        private final AppDatabase db;
        private List<ActivityRecord> allActivities;

        public HistoryViewModel(@NonNull Application application) {
            super(application);
            db = AppDatabase.getDatabase(application);
            activities = new MutableLiveData<>();
            allActivities = new ArrayList<>();
            loadActivities();
        }

        public LiveData<List<ActivityRecord>> getActivities() {
            return activities;
        }

        private void loadActivities() {
            new Thread(() -> {
                List<ActivityRecord> activityRecords = db.activityRecordDao().getAllRecords();
                activities.postValue(activityRecords); // use postValue instead of setValue for background thread
            }).start();
        }

        public List<ActivityRecord> filterActivitiesDorDate(long date) {
            List<ActivityRecord> filteredActivities = new ArrayList<>();
            for (ActivityRecord activity : allActivities) {
                if (activity.getDate() == date) {
                    filteredActivities.add(activity);
                }
            }
            return filteredActivities;
        }
    }
