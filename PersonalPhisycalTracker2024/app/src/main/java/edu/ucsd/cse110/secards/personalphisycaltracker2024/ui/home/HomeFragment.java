package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
                timerManager.startTimer();
                activitySpinner.setEnabled(false); // Disabilita lo spinner quando il timer inizia
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerManager.stopTimer(activitySpinner.getSelectedItem().toString());
                activitySpinner.setEnabled(true); // Abilita lo spinner quando il timer si ferma
            }
        });

        return view;
    }

}