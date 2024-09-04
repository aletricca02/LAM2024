package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Spinner;


import java.time.LocalDate;
import java.time.ZoneId;
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
        View rootView = inflater.inflate(R.layout.history_gallery, container, false);
        calendarView = rootView.findViewById(R.id.calendarView);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        filterSpinner = rootView.findViewById(R.id.filter_Spinner);
        recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        // Recupera l'array dei filtri da inserire nello spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.activities_filter,
                R.layout.spinner_item
        );
        // Imposta il layout del dropdown per lo spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Imposta l'adapter allo spinner
        filterSpinner.setAdapter(adapter);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Crea un oggetto LocalDate con la data selezionata
                LocalDate dataDaCalendar = LocalDate.of(year, month + 1, dayOfMonth);

                // Converti la data in una stringa nel formato desiderato (yyyy-MM-dd)
                String dataString = dataDaCalendar.toString(); // "yyyy-MM-dd" by default

                // Crea un Intent per lanciare la ActivityDetailsActivity
                Intent intent = new Intent(getActivity(), ActivityDetailsActivity.class);

                // Passa la data come stringa con l'intent
                intent.putExtra("date", dataString);
                intent.putExtra("filter", filterSpinner.getSelectedItem().toString());

                // Avvia ActivityDetailsActivity, passando i dati aggiunti all'intent.
                startActivity(intent);
            }
        });
        return rootView;
    }


}