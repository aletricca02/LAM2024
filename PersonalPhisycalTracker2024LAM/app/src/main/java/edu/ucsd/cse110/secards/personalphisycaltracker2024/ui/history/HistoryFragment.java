package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Spinner;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityAdapter;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityDetailsActivity;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityRecord;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.R;

public class HistoryFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private Spinner filterSpinner;
    private ActivityAdapter adapter;
    private List<ActivityRecord> activityList; // Ottieni questa lista dal tuo ViewModel o DAO
    private HistoryViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflating the layout for this fragment
        View rootView = inflater.inflate(R.layout.history_gallery, container, false);

        // Initialize views
        calendarView = rootView.findViewById(R.id.calendarView);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        filterSpinner = rootView.findViewById(R.id.filter_Spinner);

        // Setting up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //adapter = new ActivityAdapter(activityList != null ? activityList : new ArrayList<ActivityRecord>());
        recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                long dateInMillis = selectedDate.getTimeInMillis();
                loadActivitiesForDate(dateInMillis);

            }
        });
        return rootView;
    }

    private void loadActivitiesForDate(long dateInMillis ) {
        // Filtra activityList per la data selezionata e aggiorna l'adapter
        List<ActivityRecord> filteredList = viewModel.filterActivities(dateInMillis );
        // Pass the filtered list to ActivityDetailsActivity
        Intent intent = new Intent(getActivity(), ActivityDetailsActivity.class);
        intent.putParcelableArrayListExtra("activities", (ArrayList<? extends Parcelable>) new ArrayList<>(filteredList));
        intent.putExtra("date",  dateInMillis );
        startActivity(intent);
    }

   /* private List<ActivityRecord> getActivitiesForDate(Calendar date) {
        // Implementa la logica per filtrare le attività per data
        List<ActivityRecord> filteredList = new ArrayList<>();
        if (activityList != null) {
            for (ActivityRecord record : activityList) {
                if (isSameDay(record.getDate(), date)) {
                    filteredList.add(record);
                }
            }
        }
        return filteredList;
    }*/

    private boolean isSameDay(long date1, long date2) {
        Boolean b = false;
        if (date1 == date2)
            b = true;
        return b;
    }


}