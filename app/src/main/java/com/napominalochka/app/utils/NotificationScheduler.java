package com.napominalochka.app.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.napominalochka.app.workers.DailyNotificationReceiver;

import java.util.Calendar;

public class NotificationScheduler {

    private static final String CHANNEL_ID = "napominalochka_daily";
    private static final String CHANNEL_NAME = "Ежедневные напоминания";
    private static final String CHANNEL_DESC = "Ежедневные напоминания о любви";
    private static final int NOTIFICATION_ID = 1001;
    private static final String TAG = "NotificationScheduler";

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
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created");
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

        // Отменяем предыдущий алярм если есть
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        // Настраиваем на 10:00 каждый день
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Если сейчас уже после 10:00, планируем на завтра
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (alarmManager != null) {
            try {
                // Используем setExactAndAllowWhileIdle для более надежной доставки
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                }
                Log.d(TAG, "Daily notification scheduled for: " + calendar.getTime());

                // Планируем следующее уведомление рекурсивно
                scheduleNextDayNotification(context, calendar);

            } catch (SecurityException e) {
                Log.e(TAG, "Permission denied for scheduling exact alarms", e);
                // Fallback к обычному повторяющемуся алярму
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                );
            }
        }
    }

    private static void scheduleNextDayNotification(Context context, Calendar currentAlarm) {
        // Планируем алярм на следующий день
        Calendar nextDay = (Calendar) currentAlarm.clone();
        nextDay.add(Calendar.DAY_OF_MONTH, 1);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DailyNotificationReceiver.class);
        intent.putExtra("reschedule", true); // Флаг для перепланирования

        PendingIntent nextDayPendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID + 1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            nextDay.getTimeInMillis(),
                            nextDayPendingIntent
                    );
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            nextDay.getTimeInMillis(),
                            nextDayPendingIntent
                    );
                }
                Log.d(TAG, "Next day notification scheduled for: " + nextDay.getTime());
            } catch (SecurityException e) {
                Log.e(TAG, "Permission denied for scheduling next day alarm", e);
            }
        }
    }

    public static String getChannelId() {
        return CHANNEL_ID;
    }
}