package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.slideshow;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import android.widget.DatePicker;
import java.util.Calendar;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityDetailsVieModel;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityStats;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.Date.AppDatabase;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.R;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.databinding.FragmentChartsBinding;

public class SlideshowFragment extends Fragment {

    private AppDatabase db;
    private ExecutorService executorService;
    private Button selectDateButton;
    private SlideshowViewModel viewModel;
    private LiveData<List<ActivityStats>> activity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        db = AppDatabase.getDatabase(getContext());
        executorService = Executors.newSingleThreadExecutor();

        viewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);
        PieChart pieChart = view.findViewById(R.id.pieChart);
        BarChart barChart = view.findViewById(R.id.barChart);
        selectDateButton = view.findViewById(R.id.selectDateButton);

        selectDateButton.setOnClickListener(v -> showDatePicker());
        viewModel.getAllActivities().observe(getViewLifecycleOwner(), new Observer<List<ActivityStats>>() {
            @Override
            public void onChanged(@Nullable List<ActivityStats> activityStatsList) {
                if (activityStatsList != null) {
                    setupPieChart(pieChart, activityStatsList);
                    setupBarChart(barChart, activityStatsList);
                }
            }
        });
        return view;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, yearSelected, monthOfYear, dayOfMonth) -> {
            // Quando viene selezionato un mese
            String selectedMonth = String.format("%04d-%02d", yearSelected, monthOfYear + 1);
            // Fai qualcosa con il mese selezionato
            viewModel.loadActivitiesforMonth(selectedMonth);
        }, year, month, calendar.get(Calendar.DAY_OF_MONTH));

        // Nascondi il giorno nel DatePicker
        try {
            java.lang.reflect.Field[] datePickerDialogFields = datePickerDialog.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(datePickerDialog);
                    java.lang.reflect.Field[] datePickerFields = datePicker.getClass().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName()) || "mDayPicker".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        datePickerDialog.show();
    }


    private void setupPieChart(PieChart pieChart, List<ActivityStats> activityStatsList) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (ActivityStats stats : activityStatsList) {
            entries.add(new PieEntry(stats.total_duration, stats.activity_type));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Attività del Mese");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setHoleRadius(58f);
        pieChart.setCenterText("Attività del Mese");
        pieChart.setCenterTextSize(16f);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void setupBarChart(BarChart barChart, List<ActivityStats> activityStatsList) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        int index = 0;
        for (ActivityStats stats : activityStatsList) {
            entries.add(new BarEntry(index++, stats.total_duration));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Durata Attività");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}