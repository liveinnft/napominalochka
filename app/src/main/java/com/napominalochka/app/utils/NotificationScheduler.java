package com.napominalochka.app.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.napominalochka.app.workers.DailyNotificationReceiver;

import java.util.Calendar;

public class NotificationScheduler {
    
    private static final String CHANNEL_ID = "napominalochka_daily";
    private static final String CHANNEL_NAME = "Ежедневные напоминания";
    private static final String CHANNEL_DESC = "Ежедневные напоминания о любви";
    private static final int NOTIFICATION_ID = 1001;
    
    public static void scheduleDailyNotifications(Context context) {
        createNotificationChannel(context);
        scheduleRepeatingNotification(context);
    }
    
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    private static void scheduleRepeatingNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        Intent intent = new Intent(context, DailyNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 
                NOTIFICATION_ID, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Schedule for 10:00 AM daily
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        // If current time is past 10 AM, schedule for next day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }
    
    public static String getChannelId() {
        return CHANNEL_ID;
    }
}