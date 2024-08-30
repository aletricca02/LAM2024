package edu.ucsd.cse110.secards.personalphisycaltracker2024;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

//import edu.ucsd.cse110.secards.personalphisycaltracker2024.Notification.NotificationHelper;
//import edu.ucsd.cse110.secards.personalphisycaltracker2024.Notification.NotificationScheduler;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.databinding.ActivityMainBinding;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo.GeoHelper;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo.GeoViewModel;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo.GeofenceEntity;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import android.Manifest;
import android.widget.Toast;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {
    private GeofencingClient geofencingClient;
    private GeoHelper geofenceHelper;
    private GeoViewModel geoViewModel;
    private GoogleMap mMap;
    ActivityDetailsVieModel viewModel;
    private List<GeofenceEntity> currentGeofences;

    private TimerManager timerManager;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final int PERMISSION_REQUEST_NOTIFICATIONS = 101;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GeofenceBroadcastReceiver geofenceBroadcastReceiver;
    private PendingIntent geofencePendingIntent;
    private ArrayList<String> activitiesList;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS,            // Permesso per le notifiche
                            Manifest.permission.ACTIVITY_RECOGNITION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_NOTIFICATIONS
            );
        }

         setSupportActionBar(binding.appBarMain.toolbar);


        // Carica la lista dalle SharedPreferences
        SharedPreferences prefs = getSharedPreferences("ActivitiesPrefs", MODE_PRIVATE);
        Set<String> savedActivitiesSet = prefs.getStringSet("activitiesList", null);

        if (savedActivitiesSet != null) {
            activitiesList = new ArrayList<>(savedActivitiesSet);
        } else {
            // Inizializza con i valori predefiniti se non ci sono attività salvate
            activitiesList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.activities_array)));
        }


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_geo)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_main, new HomeFragment(), "HOME_FRAGMENT_TAG");
        transaction.commit();
        // Controlla se la notifica è già pianificata
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNotificationScheduled = prefs.getBoolean("isNotificationScheduled", false);

        if (!isNotificationScheduled) {
            schedulePeriodicNotification(1, TimeUnit.HOURS);

            // Imposta il flag che indica che la notifica è stata pianificata
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isNotificationScheduled", true);
            editor.apply();
        }

        /*geoViewModel = new ViewModelProvider(this).get(GeoViewModel.class);

        geofencingClient = LocationServices.getGeofencingClient(this);

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );
        //simulateGeofenceTransition(Geofence.GEOFENCE_TRANSITION_ENTER, "myGeofenceId");
       // fetchGeofencesAndSetup();*/
    }

    private void simulateGeofenceTransition(int geofenceTransition, String requestId) {
        // Crea un Intent per il BroadcastReceiver che gestirà le transizioni della geofence
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);

        // Inserisci manualmente i dati della transizione nell'Intent
        intent.putExtra("geofence_transition", geofenceTransition);
        intent.putExtra("geofence_request_id", requestId);

        // Invia l'Intent al BroadcastReceiver
        sendBroadcast(intent);
    }

    private void fetchGeofencesAndSetup() {
        new Thread(() -> {
            currentGeofences = geoViewModel.getGeos();

            // Dopo che currentGeofences è stato popolato, chiama setupGeofences
            runOnUiThread(() -> {
                setupGeofences();
            });
        }).start();
    }

    private void setupGeofences() {
        if (currentGeofences == null || currentGeofences.isEmpty()) {
            Log.w("Pinello", "Nessun geofence disponibile per la configurazione.");
            return;
        }

        List<Geofence> geofenceList = new ArrayList<>();
        for (GeofenceEntity entity : currentGeofences) {
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(entity.getId()) // Assicurati che `GeofenceEntity` abbia un metodo getId()
                    .setCircularRegion(entity.getLatitude(), entity.getLongitude(), entity.getRadius())
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setLoiteringDelay(1000)  // Loitering delay in millisecondi (ad esempio 30 secondi)
                    .build()
            );
        }
        Log.d("Pinello", geofenceList.get(0).toString());
        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .addGeofences(geofenceList) // Usa la lista di Geofence qui
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission(); // Richiedi i permessi necessari se non sono già stati concessi
            return;
        }

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("Pinello", "Geofence aggiunto con successo.");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Pinello", "Errore nell'aggiunta del geofence", e);
                    }
                });
    }



    private void requestLocationPermission() {
        // Richiede i permessi di localizzazione
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void schedulePeriodicNotification(long interval, TimeUnit timeUnit) {
        // Creare i dati di input
        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(
                PeriodicNotificationWorker.class,
                interval, timeUnit
        )
                .build();

        // Enqueue il lavoro periodico unico
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        "PeriodicNotificationWork", // Nome univoco per il lavoro
                        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // Sostituisce se esiste già
                        notificationWork
                );


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            activitiesList = data.getStringArrayListExtra("activitiesList");
            // Trova l'HomeFragment utilizzando il tag
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HOME_FRAGMENT_TAG");

            if (homeFragment != null) {
                // Aggiorna l'HomeFragment
                homeFragment.updateActivitiesList(activitiesList);
            } else {
                // Non è stato trovato l'HomeFragment, mostra un messaggio
                Toast.makeText(this, "HomeFragment non trovato", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Controlla se l'elemento di menu selezionato è l'action_settings
        if (id == R.id.action_settings) {
            // Crea un nuovo Bundle per passare i dati
            Bundle args = new Bundle();
            args.putStringArrayList("activitiesList", activitiesList);

            // Usa NavController per navigare e passare il Bundle
            navController.navigate(R.id.nav_manage, args);  // Passa il bundle come secondo parametro

            return true; // Indica che l'elemento è stato gestito
        }

        return super.onOptionsItemSelected(item);
    }


}