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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityAdapter;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityDetailsVieModel;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ActivityRecord;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.R;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.history.HistoryViewModel;
import kotlin.random.URandomKt;
import com.google.android.gms.maps.OnMapReadyCallback; // Importa l'interfaccia

public class GeoFragment extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GeofencingClient geofencingClient;
    private GeoHelper geofenceHelper;
    private GeoViewModel geoViewModel;
    private GoogleMap mMap;
    private boolean isViewJustCreated = true;
    ActivityDetailsVieModel viewModel;
    private List<GeofenceEntity> currentGeofences;  // Lista per tenere traccia dei geofences

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geo, container, false);
        isViewJustCreated = true;
        // Controlla se i permessi di localizzazione sono stati concessi
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permessi già concessi, aggiungi la geofence
        } else {
            // Permessi non concessi, richiedili all'utente
            requestLocationPermission2();
        }

        geoViewModel = new ViewModelProvider(this).get(GeoViewModel.class);
        geofenceHelper = new GeoHelper(getActivity());
        viewModel = new ViewModelProvider(this).get(ActivityDetailsVieModel.class);

        geofencingClient = LocationServices.getGeofencingClient(getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button addGeofenceButton = view.findViewById(R.id.add_geofence_button);
        addGeofenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCurrentLocationAsGeofence();
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
        // Richiede i permessi di localizzazione
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Toast.makeText(getActivity(), "Permessi concessi", Toast.LENGTH_SHORT).show();
                // Riprova ad eseguire le azioni che richiedono i permessi
            } else {
                Toast.makeText(getActivity(), "Permessi negati", Toast.LENGTH_SHORT).show();
                // Gestisci il caso in cui i permessi non sono stati concessi
            }
        }
    }


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
                           // lat = Math.round(lat * 1000.0) / 1000.0;
                            //lng = Math.round(lng * 1000.0) / 1000.0;

                            addGeofence(lat, lng);
                        }
                    }
                });
    }



    private void addGeofence(double lat, double lng) {
        isViewJustCreated = false;
        String geofenceId = UUID.randomUUID().toString();
        float radius = 100; // in metri

        geofenceHelper.createGeofence(geofenceId, lat, lng, radius);

        geoViewModel.addGeofence(lat, lng, radius);
     /*   geoViewModel.getGeofences().observe(getViewLifecycleOwner(), geofenceEntities -> {
            List<Geofence> geofenceList = new ArrayList<>();
            for (GeofenceEntity entity : geofenceEntities) {
                Geofence geofence = new Geofence.Builder()
                        .setRequestId(entity.getId())
                        .setCircularRegion(entity.getLatitude(), entity.getLongitude(), entity.getRadius())
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .build();
                geofenceList.add(geofence);
            }
            addGeofencesToClient(geofenceList);
        });*/
    }

    private void addGeofencesToClient(List<Geofence> geofenceList) {
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofenceList)
                .build();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //requestLocationPermission();
            return;
        }
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(aVoid -> Log.d("Geofence", "Geofences added successfully"))
                .addOnFailureListener(e -> Log.e("Geofence", "Failed to add geofences", e));
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
                // Mostra un dialogo per visualizzare o rimuovere la geofence
                showGeofenceOptionsDialog(geofence, circle);
                break;
            }
        }
    }

    private void showGeofenceOptionsDialog(GeofenceEntity geofence, Circle circle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Geofence")
                .setMessage("Scegli un'opzione per la geofence selezionata:")
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

        // Ottieni i dati delle attività per la geofence
        getActivityDataForGeofence(geofence, lineChart, stepsTextView  );

        // Crea l'AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Geofence Activities")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getActivityDataForGeofence(GeofenceEntity geofence, BarChart lineChart , TextView stepsTextView) {
        viewModel.loadActivities();
        viewModel.getAllActivities().observe(getViewLifecycleOwner(), activityRecords -> {
            Log.d("atti",activityRecords.size()+"");
            if (activityRecords != null && !activityRecords.isEmpty()) {
                // Filtra i record delle attività in base alla geofence
                List<ActivityRecord> filteredRecords = filterRecordsByGeofence(
                        activityRecords,
                        geofence.getLatitude(),
                        geofence.getLongitude(),
                        geofence.getRadius()
                );
                Log.d("atti",filteredRecords.size()+"");
                // Configura il grafico con i record filtrati
                setupBarChart(lineChart, filteredRecords, stepsTextView);
            }
        });
    }

    private List<ActivityRecord> filterRecordsByGeofence(List<ActivityRecord> records, double geofenceLat, double geofenceLng, double radius) {
        List<ActivityRecord> filteredRecords = new ArrayList<>();
        for (ActivityRecord record : records) {
            double distance = geoViewModel.haversine(geofenceLat, geofenceLng, record.getLang(), record.getLongi());
            Log.d("atti",distance+"distanza");

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
        durationDataSet.setColor(Color.BLUE);
        durationDataSet.setValueTextColor(Color.TRANSPARENT); // Nascondi i valori sopra le barre

        BarData barData = new BarData(durationDataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(activityNames));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setFitBars(true);
        barChart.invalidate();
        int totalSteps = 0;
        // Ora aggiungiamo un TextView sotto il grafico per visualizzare i passi camminati
        if (stepsMap.containsKey("Camminare")) {
            totalSteps = stepsMap.get("Camminare");
            stepsTextView.setText("Passi camminati in questa area: " + totalSteps);
        }

    }



    @Override
    public void onResume() {
        super.onResume();
        drawGeofences(); // Assicurati che i geofences siano ridisegnati quando il frammento è visibile
    }
}
