package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.UUID;

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
            requestLocationPermission();
        }

        geofenceHelper = new GeoHelper(getActivity());
        geoViewModel = new ViewModelProvider(this).get(GeoViewModel.class);

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
        requestLocationPermission2();
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
                            lat = Math.round(lat * 1000.0) / 1000.0;
                            lng = Math.round(lng * 1000.0) / 1000.0;

                            addGeofence(lat, lng);
                        }
                    }
                });
    }

    private void addGeofence(double lat, double lng) {
        isViewJustCreated = false;
        String geofenceId = UUID.randomUUID().toString();
        float radius = 90; // in metri

        Geofence geofence = geofenceHelper.createGeofence(geofenceId, lat, lng, radius);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }

        geoViewModel.addGeofence(lat, lng, radius, () -> {
            geofencingClient.addGeofences(geofenceHelper.createGeofencingRequest(geofence), geofenceHelper.getPendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("GeofenceReceiver", "geofence registrata con successo");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("GeofenceReceiver", "fallimento aggiunta geofence ");
                        }
                    });
        });
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

        drawGeofences(); // Ridisegna i geofences dopo che la mappa è pronta
    }

    private void drawGeofences() {
        if (mMap != null && currentGeofences != null) {
            mMap.clear(); // Pulisce la mappa
            for (GeofenceEntity geofence : currentGeofences) {
                LatLng latLng = new LatLng(geofence.getLatitude(), geofence.getLongitude());
                mMap.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(geofence.getRadius())
                        .strokeColor(0x220000FF)
                        .fillColor(0x220000FF)
                        .strokeWidth(2));
                mMap.addMarker(new MarkerOptions().position(latLng).title("Geofence"));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        drawGeofences(); // Assicurati che i geofences siano ridisegnati quando il frammento è visibile
    }
}
