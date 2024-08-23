package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.home;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.Date.AppDatabase;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.R;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.TimerManager;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {
    private TimerManager timerManager;
    private AppDatabase db;
    private ExecutorService executorService;
    private StepCounterManager stepCounterManager;
    int steps = 0;
    LatLng userLocation;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = AppDatabase.getDatabase(getContext());
        executorService = Executors.newSingleThreadExecutor();

        TextView timerTextView = view.findViewById(R.id.timerTextView);
        Button startButton = view.findViewById(R.id.startButton);
        Button stopButton = view.findViewById(R.id.stopButton);
        Spinner activitySpinner = view.findViewById(R.id.activitySpinner);


        /*boolean locationPermissionGranted = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        boolean backgroundLocationPermissionGranted = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!locationPermissionGranted || !backgroundLocationPermissionGranted) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }*/


        // Recupera l'array di attività
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.activities_array,
                R.layout.spinner_item
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
                    Toast.makeText(getContext(), "StepCounterManager non è disponibile", Toast.LENGTH_SHORT).show();

                } else {
                    // Il permesso è già stato concesso
                    stepCounterManager = new StepCounterManager(getContext());
                }
                String selectedActivity = activitySpinner.getSelectedItem().toString();

                if ("Camminare".equals(selectedActivity)) {
                    stepCounterManager.startListening(); // Inizia a registrare i passi
                   /* if (stepCounterManager != null) {
                        stepCounterManager.startListening(); // Inizia a registrare i passi
                    } else {
                        Toast.makeText(getContext(), "StepCounterManager non è disponibile", Toast.LENGTH_SHORT).show();
                    }*/
                }
                steps=0;
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
                activitySpinner.setEnabled(false); // Disabilita lo spinner quando il timer inizia
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

                timerManager.stopTimer(selectedActivity, steps,userLocation.latitude,userLocation.longitude);
                activitySpinner.setEnabled(true); // Abilita lo spinner quando il timer si ferma

            }
        });

        return view;
    }
}