package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.Date.AppDatabase;

public class ActivityDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_DATE = "edu.ucsd.cse110.secards.personalphisycaltracker2024.DATE";

    private ActivityAdapter activityAdapter;
    private long date;
    private TextView detailsTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        detailsTextView = findViewById(R.id.ActivityTextView);
        // Ricevi i dati dall'intent
        long date = getIntent().getLongExtra("date", -1);
        List<ActivityRecord> activities = getIntent().getParcelableArrayListExtra("activities");

        // Visualizza i dati
        StringBuilder details = new StringBuilder();
        details.append("Date: ").append(date).append("\n\n");

        if (activities != null) {
            for (ActivityRecord activity : activities) {
                details.append(activity.toString()).append("\n");
            }
        } else {
            details.append("No activities found for this date.");
        }

        detailsTextView.setText(details.toString());
    }
}

