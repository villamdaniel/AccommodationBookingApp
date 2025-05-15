package com.example.accommodationbookingapp;

import android.app.job.JobParameters;
import android.app.job.JobService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationJobService extends JobService {

    private static final String CHANNEL_ID = "accommodation_channel";

    @Override
    public boolean onStartJob(JobParameters params) {
        // Értesítés küldése
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Ütemezett értesítés")
                .setContentText("Ez az értesítés a JobScheduler segítségével érkezett.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat.from(this).notify(3001, builder.build());

        // Nincs további munka → false
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // Megszakítás esetén
        return false;
    }
}
