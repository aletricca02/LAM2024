package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ActivityDetailsActivity extends AppCompatActivity {
    private ActivityAdapter activityAdapter;
    private TextView detailsTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        detailsTextView = findViewById(R.id.ActivityTextView);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ActivityDetailsVieModel viewModel = new ViewModelProvider(this).get(ActivityDetailsVieModel.class);

        // Ricevi i dati dall'intent
        String dateString = getIntent().getStringExtra("date");
        String filter = getIntent().getStringExtra("filter");
        assert filter != null;
        if(filter.equals("Nessun Filtro")) {
            viewModel.loadActivitiesforDate(dateString);
        }else{
            viewModel.loadActivitiesforDateAndFilter(dateString,filter);

        }
        viewModel.getAllActivities().observe(this, activities -> {
            if (activities != null && !activities.isEmpty()) {
                detailsTextView.setText("Date: "+dateString);
                activityAdapter = new ActivityAdapter(activities);
                recyclerView.setAdapter(activityAdapter);
            } else {
                detailsTextView.setText("No activities found for this date.");
            }
        });
    }
}