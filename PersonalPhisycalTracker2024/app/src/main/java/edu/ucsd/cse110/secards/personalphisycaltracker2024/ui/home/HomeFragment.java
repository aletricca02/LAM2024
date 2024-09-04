package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.home;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.Date.AppDatabase;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.R;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.TimerManager;

public class HomeFragment extends Fragment {
    private TimerManager timerManager;
    private AppDatabase db;
    private ExecutorService executorService;
    private StepCounterManager stepCounterManager;
    int steps = 0;
    private boolean isGoing = false;
    LatLng userLocation;
    private ArrayList<String> activitiesList;
    private ArrayAdapter<String> adapter;
    private Spinner activitySpinner;
    private MutableLiveData<ArrayList<String>> activities;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = AppDatabase.getDatabase(getContext());
        executorService = Executors.newSingleThreadExecutor();
       activities = new MutableLiveData<>();

        TextView timerTextView = view.findViewById(R.id.timerTextView);
        Button startButton = view.findViewById(R.id.startButton);
        Button stopButton = view.findViewById(R.id.stopButton);
        activitySpinner = view.findViewById(R.id.activitySpinner);

        loadActivitiesList();

        adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item,
                activitiesList
        );

        // Imposta il layout del dropdown per lo spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Imposta l'adapter allo spinner
        activitySpinner.setAdapter(adapter);

        // Inizializza il TimerManager
        timerManager = new TimerManager(timerTextView, db, activitySpinner.getSelectedItem().toString(), executorService);
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACTIVITY_RECOGNITION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Non hai concesso i permessi, vai nelle impostazioni e concedili!", Toast.LENGTH_SHORT).show();


                } else {
                    // Il permesso è già stato concesso
                    stepCounterManager = new StepCounterManager(getContext());

                    String selectedActivity = activitySpinner.getSelectedItem().toString();

                    if ("Camminare".equals(selectedActivity)) {
                        stepCounterManager.startListening(); // Inizia a registrare i passi
                    }
                    steps = 0;
                    //salva la posizione di dove sta svolgendo l'attività l'utente.
                    LocationServices.getFusedLocationProviderClient(getActivity()).getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    }
                                }
                            });
                    timerManager.startTimer();
                    isGoing = true;
                    activitySpinner.setEnabled(false); // Disabilita lo spinner quando il timer inizia
                }
                }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedActivity = activitySpinner.getSelectedItem().toString();
                if ("Camminare".equals(selectedActivity)) {
                    if (stepCounterManager != null) {
                        stepCounterManager.stopListening(); // Ferma la registrazione dei passi
                        steps = stepCounterManager.getStepCount();
                    }
                }
                if (isGoing) {
                    timerManager.stopTimer(selectedActivity, steps, userLocation.latitude, userLocation.longitude);
                    isGoing = false;
                }
                activitySpinner.setEnabled(true); // Abilita lo spinner quando il timer si ferma

            }
        });
        return view;
    }


    // Metodo per aggiornare la lista delle attività disponibili
    public void updateActivitiesList(ArrayList<String> updatedActivitiesList) {
        activitiesList.clear();
        activitiesList.addAll(updatedActivitiesList);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
       // saveActivitiesList(); // Salva la lista aggiornata
    }

   /* // Metodo per salvare la lista delle attività in SharedPreferences
    private void saveActivitiesList() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("activitiesList", new HashSet<>(activitiesList));
        editor.apply();
        loadActivitiesList();

    }*/

    // Metodo per caricare la lista delle attività da SharedPreferences
    public void loadActivitiesList() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> savedActivitiesSet = prefs.getStringSet("activitiesList", null);
        if (savedActivitiesSet != null) {
            activitiesList = new ArrayList<>(savedActivitiesSet);
        } else {
            activitiesList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.activities_array)));
        }
    }
}