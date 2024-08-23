package edu.ucsd.cse110.secards.personalphisycaltracker2024;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import edu.ucsd.cse110.secards.personalphisycaltracker2024.Date.AppDatabase;
//import edu.ucsd.cse110.secards.personalphisycaltracker2024.Notification.NotificationHelper;
//import edu.ucsd.cse110.secards.personalphisycaltracker2024.Notification.NotificationScheduler;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.databinding.ActivityMainBinding;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Configuration;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    private TimerManager timerManager;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final int PERMISSION_REQUEST_NOTIFICATIONS = 101;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;



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

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_geo)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        schedulePeriodicNotification(15, TimeUnit.MINUTES);

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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_NOTIFICATIONS) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.POST_NOTIFICATIONS)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permesso per le notifiche concesso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permesso per le notifiche negato", Toast.LENGTH_SHORT).show();
                    }
                } else if (permissions[i].equals(Manifest.permission.ACTIVITY_RECOGNITION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permesso per l'attività fisica concesso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permesso per l'attività fisica negato", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permesso concesso
                setupNotifications();
            } else {
                // Permesso negato
                Toast.makeText(this, "Permission denied. Notifications cannot be sent.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupNotifications() {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.createNotificationChannel();
        // Pianifica le notifiche o invia una notifica di esempio
        notificationHelper.sendNotification("Reminder", "It's time to record your activity.");
    }*/


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
}