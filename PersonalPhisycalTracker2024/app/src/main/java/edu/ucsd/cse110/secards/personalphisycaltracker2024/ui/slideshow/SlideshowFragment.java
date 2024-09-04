package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.slideshow;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import android.widget.DatePicker;
import java.util.Calendar;
import android.os.Bundle;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityAdapter;
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
    private ViewSwitcher viewSwitcher;
    private ImageButton prevButton, nextButton;
    private TextView textViewNoData;
    private String selectedDate = "";
    private PieChart pieChart;
    private BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        db = AppDatabase.getDatabase(getContext());
        executorService = Executors.newSingleThreadExecutor();

        viewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);

        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        viewSwitcher = view.findViewById(R.id.viewSwitcher);
        selectDateButton = view.findViewById(R.id.selectDateButton);
        prevButton = view.findViewById(R.id.prevButton);
        nextButton = view.findViewById(R.id.nextButton);
        textViewNoData = view.findViewById(R.id.textViewNoData);
        selectDateButton.setOnClickListener(v -> showDatePicker());

        prevButton.setOnClickListener(v -> {
            viewSwitcher.showPrevious();
            updateGraphVisibility();
        });

        nextButton.setOnClickListener(v -> {
            viewSwitcher.showNext();
            updateGraphVisibility();
        });
        viewModel.loadActivitiesforDay(selectedDate);
        //aggiorniamo i grafici
        viewModel.getAllActivitiesForDay().observe(getViewLifecycleOwner(), activities -> {
            updateCharts(activities);
        });

        return view;
    }

    private void updateGraphVisibility() {
        View currentView = viewSwitcher.getCurrentView();
        if (currentView instanceof PieChart) {
            setupPieChart(pieChart, viewModel.getAllActivitiesForDay().getValue());
        } else if (currentView instanceof BarChart) {
            setupBarChart(barChart, viewModel.getAllActivitiesForDay().getValue());
        }
    }
    /*
     Questo metodo gestisce l'aggiornamento dei grafici basato sui dati disponibili.
     */
    private void updateCharts(List<ActivityStats> activityStatsList) {
        if (viewSwitcher.getCurrentView() == pieChart) {
            setupPieChart(pieChart, activityStatsList);
        } else if (viewSwitcher.getCurrentView() == barChart) {
            setupBarChart(barChart, activityStatsList);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, yearSelected, monthOfYear, dayOfMonth) -> {
            selectedDate = String.format("%04d-%02d-%02d", yearSelected, monthOfYear + 1, dayOfMonth);
            viewModel.loadActivitiesforDay(selectedDate);
        }, year, month, calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void setupPieChart(PieChart pieChart, List<ActivityStats> activityStatsList) {
        if (activityStatsList == null || activityStatsList.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            textViewNoData.setVisibility(View.VISIBLE);
        } else {
            textViewNoData.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);

            ArrayList<PieEntry> entries = new ArrayList<>();
            for (ActivityStats stats : activityStatsList) {
                entries.add(new PieEntry(stats.total_duration, ""));
            }

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setDrawValues(true);
            dataSet.setValueTextSize(12f);

            // Formatter che mostra solo le percentuali (senza i nomi)
            dataSet.setValueFormatter(new PercentFormatter(pieChart));

            PieData data = new PieData(dataSet);
            dataSet.setValueTextColor(Color.WHITE); // Colore del testo
            pieChart.setData(data);

            // Imposta la legenda per mostrare i nomi delle attività
            Legend legend = pieChart.getLegend();
            legend.setEnabled(true);
            legend.setTextColor(Color.BLACK);
            legend.setTextSize(10f);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);

            // Aggiungi i nomi delle attività alla legenda
            List<String> activityNames = new ArrayList<>();
            for (ActivityStats stats : activityStatsList) {
                activityNames.add(stats.activity_type);  // Usa i nomi per la legenda
            }

            LegendEntry[] legendEntries = new LegendEntry[activityNames.size()];
            for (int i = 0; i < activityNames.size(); i++) {
                LegendEntry entry = new LegendEntry();
                entry.label = activityNames.get(i); // Imposta il nome dell'attività
                entry.formColor = ColorTemplate.MATERIAL_COLORS[i % ColorTemplate.COLORFUL_COLORS.length]; // Imposta il colore
                legendEntries[i] = entry;
            }
            pieChart.getLegend().setCustom(legendEntries); // Assegna la legenda personalizzata

            // Configurazione del grafico
            pieChart.setUsePercentValues(true);
            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText("% Attività svolte");
            pieChart.setCenterTextColor(Color.BLACK);
            pieChart.setCenterTextSize(16f);
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColor(Color.TRANSPARENT);
            pieChart.setTransparentCircleAlpha(0);
            pieChart.setHoleRadius(40f);

            pieChart.animateY(1000);
            pieChart.invalidate();
        }
    }


    private void setupBarChart(BarChart barChart, List<ActivityStats> activityStatsList) {
        if (activityStatsList == null || activityStatsList.isEmpty()) {
            barChart.setVisibility(View.GONE);
            textViewNoData.setVisibility(View.VISIBLE);
        } else {
            textViewNoData.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);

            ArrayList<BarEntry> entries = new ArrayList<>();
            List<String> activityNames = new ArrayList<>();
            int index = 0;

            List<Integer> colors = new ArrayList<>();
            for (ActivityStats stats : activityStatsList) {
                entries.add(new BarEntry(index++, stats.total_duration/1000)); // Totale durata in secondi
                activityNames.add(stats.getActivity_type());

                colors.add(ColorTemplate.MATERIAL_COLORS[index % ColorTemplate.COLORFUL_COLORS.length]);
            }

            BarDataSet dataSet = new BarDataSet(entries, "Durata Attività");
            dataSet.setColors(colors);
            dataSet.setDrawValues(false);  // Disabilita la visualizzazione dei valori sopra le barre

            List<LegendEntry> legendEntries = new ArrayList<>();
            for (int i = 0; i < activityNames.size(); i++) {
                LegendEntry entry = new LegendEntry();
                entry.label = activityNames.get(i);
                entry.formColor = colors.get(i);
                legendEntries.add(entry);
            }

            Legend legend = barChart.getLegend();
            legend.setCustom(legendEntries);

            BarData data = new BarData(dataSet);
            barChart.setData(data);

            YAxis yAxis = barChart.getAxisLeft();
            barChart.getAxisRight().setEnabled(false);
            barChart.getXAxis().setEnabled(false);
            barChart.getDescription().setEnabled(false);
            barChart.setFitBars(true);
            barChart.animateY(1000);
            barChart.invalidate();
        }
    }

}