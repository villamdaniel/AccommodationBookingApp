package com.example.accommodationbookingapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class HomeActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "accommodation_channel";
    private static final int REQUEST_LOCATION = 42;

    private TextView welcomeTextView;
    private Button buttonAvailable, buttonCheapest, buttonExpensive, buttonAddAccommodation,
            buttonNotify, buttonJobScheduler, buttonLocation;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeTextView = findViewById(R.id.welcomeText);
        buttonAvailable = findViewById(R.id.buttonAvailable);
        buttonCheapest = findViewById(R.id.buttonCheapest);
        buttonExpensive = findViewById(R.id.buttonExpensive);
        buttonNotify = findViewById(R.id.buttonNotify);
        buttonJobScheduler = findViewById(R.id.buttonJobScheduler);
        buttonLocation = findViewById(R.id.buttonLocation);
        buttonAddAccommodation = findViewById(R.id.buttonAddAccommodation);

        // Android 13+ értesítési engedélykérés
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Értesítési csatorna létrehozása
        createNotificationChannel();

        // Animáció
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        welcomeTextView.startAnimation(fadeIn);

        // Helymeghatározási kliens inicializálása
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Lekérdezés gombok
        buttonAvailable.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BrowseActivity.class);
            intent.putExtra("queryType", "available");
            startActivity(intent);
        });

        buttonCheapest.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BrowseActivity.class);
            intent.putExtra("queryType", "cheapest");
            startActivity(intent);
        });

        buttonExpensive.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BrowseActivity.class);
            intent.putExtra("queryType", "expensive");
            startActivity(intent);
        });

        // Értesítés gomb
        buttonNotify.setOnClickListener(v -> sendNotification());

        // JobScheduler gomb
        buttonJobScheduler.setOnClickListener(v -> {
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo = new JobInfo.Builder(
                    1,
                    new ComponentName(getPackageName(), NotificationJobService.class.getName())
            )
                    .setMinimumLatency(5000)
                    .setOverrideDeadline(10000)
                    .build();
            scheduler.schedule(jobInfo);
        });

        // GPS helyzet gomb
        buttonLocation.setOnClickListener(v -> requestLocationPermissionAndFetch());

        buttonAddAccommodation.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddAccommodationActivity.class);
            startActivity(intent);
        });
    }

    private void requestLocationPermissionAndFetch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            fetchLastLocation();
        }
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Nincs helymeghatározási engedély.", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        String msg = String.format("Pozíciód: Lat=%.5f, Lon=%.5f",
                                location.getLatitude(), location.getLongitude());
                        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(HomeActivity.this, "Helyzet nem elérhető.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLastLocation();
        } else {
            Toast.makeText(this, "Helymeghatározás engedély elutasítva.", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void sendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Szállásfoglaló App")
                .setContentText("Ez egy kézi értesítés, amit a gomb megnyomásával küldtél!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1002, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "AccommodationChannel";
            String description = "Csatorna szállás értesítésekhez";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
