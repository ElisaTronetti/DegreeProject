package com.example.degreeapp.ui;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.degreeapp.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String description = bundle.getString("text");
            if (description != null) {
                Log.e("NOT", "Notification");
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify")
                        .setSmallIcon(R.drawable.ic_help_outline_black)
                        .setContentTitle("Ricorda che...")
                        .setContentText(description)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

                notificationManagerCompat.notify(200, builder.build());
            }
        }
    }


}
