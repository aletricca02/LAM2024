package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<String>> activitiesList = new MutableLiveData<>();
    ArrayList<String> initialActivities = new ArrayList<>();

    public HomeViewModel() {
        // Carica la lista iniziale delle attività// Puoi caricare i dati iniziali da SharedPreferences o altro
        activitiesList.setValue(initialActivities);
    }

    public LiveData<ArrayList<String>> getActivitiesList() {
        return activitiesList;
    }

    public void updateActivities(ArrayList<String> updatedActivitiesList) {
        activitiesList.setValue(updatedActivitiesList);
    }
}
