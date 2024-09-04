package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.databinding.ActivityMainBinding;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import android.Manifest;
import android.widget.Toast;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {
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
        //richiediamo i permessi.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.ACTIVITY_RECOGNITION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_NOTIFICATIONS
            );
        }

         setSupportActionBar(binding.appBarMain.toolbar);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> savedActivitiesSet = prefs.getStringSet("activitiesList", null);
        if (savedActivitiesSet != null) {
            activitiesList = new ArrayList<>(savedActivitiesSet);
        } else {
            // Inizializza con i valori predefiniti se non ci sono attività salvate
            activitiesList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.activities_array)));
        }

        //setup navigation
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
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
        Log.d("notificaaa",""+prefs.getBoolean("isNotificationScheduled", false));
        boolean isNotificationScheduled = prefs.getBoolean("isNotificationScheduled", false);

        if (!isNotificationScheduled) {
            schedulePeriodicNotification(3, TimeUnit.HOURS);
            // Imposta il flag che indica che la notifica è stata pianificata
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isNotificationScheduled", true);
            editor.apply();
        }

    }


    private void schedulePeriodicNotification(long interval, TimeUnit timeUnit) {
        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(
                PeriodicNotificationWorker.class,
                interval, timeUnit
        )
                .build();
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        "PeriodicNotificationWork", // Nome univoco per il lavoro
                        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // Sostituisce se esiste già
                        notificationWork
                );


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

   private void saveActivitiesToPreferences() {
       SharedPreferences prefs = getSharedPreferences("ActivitiesPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> activitiesSet = new HashSet<>(activitiesList);  // Converti la lista in un Set per salvarla
        editor.putStringSet("activitiesList", activitiesSet);
        editor.apply();  // Applica le modifiche
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            activitiesList = data.getStringArrayListExtra("activitiesList");
            saveActivitiesToPreferences();  // Salva la lista aggiornata
        /*
            // Trova l'HomeFragment utilizzando il tag
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HOME_FRAGMENT_TAG");
            if (homeFragment != null) {
                // Aggiorna l'HomeFragment
               // homeFragment.updateActivitiesList(activitiesList);
            } else {
                // Non è stato trovato l'HomeFragment, mostra un messaggio
                Toast.makeText(this, "HomeFragment non trovato", Toast.LENGTH_SHORT).show();
            }*/
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