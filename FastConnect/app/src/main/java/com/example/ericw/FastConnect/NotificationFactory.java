package com.example.ericw.fastconnect;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationFactory {
    private static final String NOTIFICATION =
            "com.example.ericw.fastconnect.SwitchWifiService.notification";

    public static Notification createForegroundNotification(Service service, Class<?> cls) {
        Intent notificationIntent = new Intent(service.getApplicationContext(), MainActivity.class);
        notificationIntent.setAction(NOTIFICATION);  // A string containing the action name
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(service, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(service)
                .setContentTitle(service.getResources().getString(R.string.app_name))
                .setTicker(service.getResources().getString(R.string.app_name))
                .setContentText(service.getResources().getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentIntent(contentPendingIntent)
                .setOngoing(true)
                .build();
        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;

        return notification;
    }
}
