package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.activity.OnBackPressedCallback;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.R;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.home.HomeFragment;

public class ManageActivitiesFragment extends Fragment {

    private ArrayList<String> activitiesList;
    private ArrayAdapter<String> adapter;
    private ListView activitiesListView;
    private EditText newActivityEditText;
    private Button addButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_manage_activities, container, false);

        // Recupera la lista passata dall'Activity principale
        if (getArguments() != null) {
            activitiesList = getArguments().getStringArrayList("activitiesList");
        }

        // Setup dell'ArrayAdapter
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, activitiesList);

        activitiesListView = view.findViewById(R.id.activitiesListView);
        activitiesListView.setAdapter(adapter);

        newActivityEditText = view.findViewById(R.id.newActivityEditText);
        addButton = view.findViewById(R.id.addButton);

        // Aggiungi una nuova attività
        addButton.setOnClickListener(v -> {
            String newActivity = newActivityEditText.getText().toString().trim();
            if (!newActivity.isEmpty() && !activitiesList.contains(newActivity)) {
                activitiesList.add(newActivity);
                adapter.notifyDataSetChanged();
                newActivityEditText.setText(""); // Resetta il campo di testo
            }
        });

        // Rimuovi un'attività con long click
        activitiesListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            String activityToRemove = activitiesList.get(position);
            if (!activityToRemove.equals("Camminare") && !activityToRemove.equals("Sedersi") && !activityToRemove.equals("Guidare")) { // Evita di rimuovere attività predefinite
                activitiesList.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Attività rimossa dalle possibili scelte", Toast.LENGTH_SHORT).show();

                return true;
            }
            Toast.makeText(getContext(), "Non puoi rimuovere le attività predefinite", Toast.LENGTH_SHORT).show();
            return false;
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Salva la lista aggiornata nelle SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("activitiesList", new HashSet<>(activitiesList));
        editor.apply();
    }
}