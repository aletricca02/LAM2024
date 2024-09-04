package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityDetailsVieModel;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityRecord;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.R;
import com.google.android.gms.maps.OnMapReadyCallback;

public class GeoFragment extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GeofencingClient geofencingClient;
    private GeoHelper geofenceHelper;
    private GeoViewModel geoViewModel;
    private GoogleMap mMap;
    ActivityDetailsVieModel viewModel;
    private List<GeofenceEntity> currentGeofences;  // Lista per tenere traccia dei geofences


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geo, container, false);
        // Controlla se i permessi di localizzazione sono stati concessi
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            // Permessi non concessi, richiedili all'utente
            requestLocationPermission2();
        }

        geoViewModel = new ViewModelProvider(this).get(GeoViewModel.class);
        viewModel = new ViewModelProvider(this).get(ActivityDetailsVieModel.class);
        geofencingClient = LocationServices.getGeofencingClient(getActivity());
        geofenceHelper = new GeoHelper(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button addGeofenceButton = view.findViewById(R.id.add_geofence_button);
        addGeofenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=29){
                    if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_BACKGROUND_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                        addCurrentLocationAsGeofence();
                    }else {
                        requestLocationPermission2();
                    }
                }else{
                    addCurrentLocationAsGeofence();

                }
            }
        });



        geoViewModel.getGeofences().observe(getViewLifecycleOwner(), new Observer<List<GeofenceEntity>>() {
            @Override
            public void onChanged(List<GeofenceEntity> geofences) {
                currentGeofences = geofences; // Salva i geofences nella variabile locale
                drawGeofences();
            }
        });

        return view;
    }


    private void requestLocationPermission() {
        // Richiede i permessi di localizzazione
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void requestLocationPermission2() {
        // Richiede i permessi di localizzazione (Background, esso deve essere chiesto separato dagli altri)
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    break;
                }
            }
        }
    }

    //Disegniamo attorno alla posizione attuale una Geofence
    private void addCurrentLocationAsGeofence() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        LocationServices.getFusedLocationProviderClient(getActivity())
                .getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();

                            addGeofence(lat, lng);
                        }
                    }
                });
    }



    private void addGeofence(double lat, double lng) {
        String geofenceId = UUID.randomUUID().toString();
        float radius = 100; // in metri
        Geofence geofence = geofenceHelper.createGeofence(geofenceId,lat,lng,radius);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        geofenceHelper.createGeofence(geofenceId, lat, lng, radius);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        if (!geoViewModel.isInside(lat, lng, radius)) {
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Geofence", "Geofence added");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String errorMessage = geofenceHelper.getErrorString(e);
                            Log.d("Geofence", errorMessage);
                        }
                    });
        }
        geoViewModel.addGeofence(lat, lng, radius);

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            LocationServices.getFusedLocationProviderClient(getActivity()).getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                            }
                        }
                    });
        } else {
            requestLocationPermission();
        }

        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                handleGeofenceClick(circle);
            }
        });
        drawGeofences(); // Ridisegna i geofences dopo che la mappa è pronta
    }

    private void handleGeofenceClick(Circle circle) {
        // Trova la geofence corrispondente
        for (GeofenceEntity geofence : currentGeofences) {
            LatLng latLng = new LatLng(geofence.getLatitude(), geofence.getLongitude());
            if (circle.getCenter().equals(latLng)) {
                // Mostra un dialogo per visualizzare i grafici o rimuovere la geofence
                showGeofenceOptionsDialog(geofence, circle);
                break;
            }
        }
    }

    private void showGeofenceOptionsDialog(GeofenceEntity geofence, Circle circle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Hai cliccato su una Geofence!")
                .setMessage("Scegli un'opzione per l'area selezionata:")
                .setPositiveButton("Visualizza Dati", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Visualizza i dati della geofence
                        showGeofenceData(geofence);
                    }
                })
                .setNegativeButton("Rimuovi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Rimuove la geofence
                        removeGeofence(geofence, circle);
                    }
                });
        builder.create().show();
    }


    private void removeGeofence(GeofenceEntity geofence, Circle circle) {
        // Rimuovi la geofence dal GeofencingClient
        geofencingClient.removeGeofences(List.of(geofence.getId())).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Geofence rimossa", Toast.LENGTH_SHORT).show();
                geoViewModel.removeGeofence(geofence);
                circle.remove(); // Rimuovi anche il cerchio dalla mappa
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Geofence", "Errore nella rimozione della geofence", e);
                Toast.makeText(getActivity(), "Errore nella rimozione della geofence", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawGeofences() {
        if (mMap != null && currentGeofences != null) {
            mMap.clear(); // Pulisce la mappa
            for (GeofenceEntity geofence : currentGeofences) {
                LatLng latLng = new LatLng(geofence.getLatitude(), geofence.getLongitude());
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(geofence.getRadius())
                        .strokeColor(0x220000FF)
                        .fillColor(0x220000FF)
                        .strokeWidth(2)
                        .clickable(true)); // Rendi il cerchio cliccabile

                mMap.addMarker(new MarkerOptions().position(latLng).title("Geofence"));
            }
        }
    }

    private void showGeofenceData(GeofenceEntity geofence) {
        // Crea il layout per l'AlertDialog
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_geofence_data, null);
        // Trova il LineChart nel layout
        BarChart lineChart = dialogView.findViewById(R.id.line_chart);
        TextView stepsTextView = dialogView.findViewById(R.id.stepsTextView);
        Button switchToDailyButton = dialogView.findViewById(R.id.switch_to_daily_button);
        // Ottieni i dati delle attività per la geofence
        getActivityDataForGeofence(geofence, lineChart, stepsTextView  );

        // Listener per il bottone che mostra il grafico giornaliero
        switchToDailyButton.setOnClickListener(new View.OnClickListener() {
            boolean showingDailyData = false;

            @Override
            public void onClick(View v) {
                showingDailyData = !showingDailyData;
                if (showingDailyData) {
                    // Mostra i dati giornalieri
                    switchToDailyButton.setText("Mostra Grafico Totale");
                    getDailyActivityDataForGeofenceToday(geofence, lineChart, stepsTextView);
                } else {
                    // Mostra i dati totali
                    switchToDailyButton.setText("Mostra Grafico Giornaliero");
                    getActivityDataForGeofence(geofence, lineChart, stepsTextView);
                }
            }
        });


        // Crea l'AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Grafico delle attività svolte nella geofence")
                .setView(dialogView)
                .setIcon(R.mipmap.ic_launcher_round)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getActivityDataForGeofence(GeofenceEntity geofence, BarChart lineChart , TextView stepsTextView) {
        viewModel.loadActivities();
        viewModel.getAllActivities().observe(getViewLifecycleOwner(), activityRecords -> {
            if (activityRecords != null && !activityRecords.isEmpty()) {
                // Filtra i record delle attività in base alla geofence
                List<ActivityRecord> filteredRecords = filterRecordsByGeofence(
                        activityRecords,
                        geofence.getLatitude(),
                        geofence.getLongitude(),
                        geofence.getRadius()
                );
                // Configura il grafico con i record filtrati
                setupBarChart(lineChart, filteredRecords, stepsTextView);
            }
        });
    }

    private void getDailyActivityDataForGeofenceToday(GeofenceEntity geofence, BarChart barChart, TextView stepsTextView) {
        LocalDate dataDaCalendar = LocalDate.now();
        String dataString = dataDaCalendar.toString();
        viewModel.loadActivitiesforDate(dataString);
        viewModel.getAllActivities().observe(getViewLifecycleOwner(), activityRecords -> {
            if (activityRecords != null && !activityRecords.isEmpty()) {
                List<ActivityRecord> filteredRecords = filterRecordsByGeofence(
                        activityRecords,
                        geofence.getLatitude(),
                        geofence.getLongitude(),
                        geofence.getRadius()
                ).stream().filter(record -> record.getDate().equals(dataString))  // Aggiungi questo filtro
                        .collect(Collectors.toList());
                setupBarChart(barChart, filteredRecords, stepsTextView);
            }else {
                // Se non ci sono attività per oggi, assicurati che il grafico sia vuoto
                barChart.clear();
                stepsTextView.setText("Nessuna attività per oggi.");
            }
        });
    }

    private List<ActivityRecord> filterRecordsByGeofence(List<ActivityRecord> records, double geofenceLat, double geofenceLng, double radius) {
        List<ActivityRecord> filteredRecords = new ArrayList<>();
        for (ActivityRecord record : records) {
            double distance = geoViewModel.androidLocationDistance(geofenceLat, geofenceLng, record.getLang(), record.getLongi());
            if (distance <= radius) {
                filteredRecords.add(record);
            }
        }
        return filteredRecords;
    }

    private void setupBarChart(BarChart barChart, List<ActivityRecord> activityRecords, TextView stepsTextView) {
        Map<String, Float> durationMap = new HashMap<>();
        Map<String, Integer> stepsMap = new HashMap<>();

        for (ActivityRecord record : activityRecords) {
            String activityName = record.getActivityName();
            float durationInSeconds = record.getDuration() / 1000f;

            // Somma la durata per ogni attività
            durationMap.put(activityName, durationMap.getOrDefault(activityName, 0f) + durationInSeconds);

            // Somma i passi per l'attività "Camminare"
            if (activityName.equalsIgnoreCase("Camminare")) {
                stepsMap.put(activityName, stepsMap.getOrDefault(activityName, 0) + record.steps);
            }
        }

        List<BarEntry> durationEntries = new ArrayList<>();
        List<String> activityNames = new ArrayList<>(durationMap.keySet());
        // Creazione delle entry per le durate (solo in secondi)
        for (int i = 0; i < activityNames.size(); i++) {
            String activityName = activityNames.get(i);
            durationEntries.add(new BarEntry(i, durationMap.get(activityName)));
        }

        BarDataSet durationDataSet = new BarDataSet(durationEntries, "Durata (s)");
        durationDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        durationDataSet.setValueTextColor(Color.TRANSPARENT); // Nascondi i valori sopra le barre

        BarData barData = new BarData(durationDataSet);
        barChart.setData(barData);

        // Imposta il colore delle etichette sull'asse Y
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        barChart.getAxisRight().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(activityNames));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setFitBars(true);
        barChart.invalidate();
        // Aggiorna il conteggio totale dei passi per la visualizzazione giornaliera
        if (stepsMap.containsKey("Camminare")) {
            int totalSteps = stepsMap.get("Camminare");
            stepsTextView.setText("Passi camminati in questa area: " + totalSteps);
        } else {
            stepsTextView.setText("Passi camminati in questa area: 0");
        }

    }



    @Override
    public void onResume() {
        super.onResume();
        drawGeofences();
        // Assicuriamo che i geofences siano ridisegnati quando il frammento è visibile
    }
}
