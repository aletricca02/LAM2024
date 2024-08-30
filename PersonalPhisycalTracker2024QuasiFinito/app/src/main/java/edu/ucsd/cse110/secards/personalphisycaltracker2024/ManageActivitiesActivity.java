package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;

public class ManageActivitiesActivity extends AppCompatActivity {

    private ArrayList<String> activitiesList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_activities);

        // Recupera la lista e l'adapter passati dall'Activity principale
        activitiesList = getIntent().getStringArrayListExtra("activitiesList");

        // Setup dell'ArrayAdapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, activitiesList);

        ListView activitiesListView = findViewById(R.id.activitiesListView);
        activitiesListView.setAdapter(adapter);

        EditText newActivityEditText = findViewById(R.id.newActivityEditText);
        Button addButton = findViewById(R.id.addButton);

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
        activitiesListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String activityToRemove = activitiesList.get(position);
            if (!activityToRemove.equals("Camminare") && !activityToRemove.equals("Sedersi") && !activityToRemove.equals("Guidare") ) { // Evita di rimuovere attività predefinite
                activitiesList.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Attività rimossa dalle possibili scelte", Toast.LENGTH_SHORT).show();

                return true;
            }
            Toast.makeText(this, "Non puoi rimuovere le attività predefinite", Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        // Salva la lista aggiornata nelle SharedPreferences
        SharedPreferences prefs = getSharedPreferences("ActivitiesPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Log.d("HomeFragment",new HashSet<>(activitiesList).toString());
        editor.putStringSet("activitiesList", new HashSet<>(activitiesList));

        editor.apply();

        // Ritorna la lista aggiornata alla MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("activitiesList", activitiesList);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}