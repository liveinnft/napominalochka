package com.napominalochka.app.workers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.napominalochka.app.R;
import com.napominalochka.app.ui.main.MainActivity;
import com.napominalochka.app.utils.NotificationScheduler;

public class DailyNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Daily notification received");

        showDailyNotification(context);

        // Если это запрос на перепланирование, планируем следующее уведомление
        boolean reschedule = intent.getBooleanExtra("reschedule", false);
        if (reschedule) {
            NotificationScheduler.scheduleDailyNotifications(context);
        }
    }

    private void showDailyNotification(Context context) {
        // Создаем intent для открытия приложения при нажатии на уведомление
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Создаем уведомление
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                NotificationScheduler.getChannelId()
        )
                .setSmallIcon(R.drawable.ic_heart)  // Убедитесь что иконка существует
                .setContentTitle("💕 Напоминалочка скучает!")
                .setContentText("Привет, любимая! Батарейка настроения нуждается в зарядке!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Привет, моя любимая! 💕 Новая порция любви и сюрпризов ждет тебя в приложении. Не забудь зарядить батарейку настроения! Я скучаю и жду тебя! 🥰"));

        // Показываем уведомление
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            try {
                notificationManager.notify(1001, builder.build());
                Log.d(TAG, "Daily notification shown successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to show notification", e);
            }
        } else {
            Log.e(TAG, "NotificationManager is null");
        }
    }
}