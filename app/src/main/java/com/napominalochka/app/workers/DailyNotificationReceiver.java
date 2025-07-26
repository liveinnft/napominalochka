package com.napominalochka.app.workers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.napominalochka.app.R;
import com.napominalochka.app.ui.main.MainActivity;
import com.napominalochka.app.utils.NotificationScheduler;

public class DailyNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        showDailyNotification(context);
    }

    private void showDailyNotification(Context context) {
        // Create intent for when notification is clicked
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, 
                NotificationScheduler.getChannelId()
        )
                .setSmallIcon(R.drawable.ic_heart)
                .setContentTitle(context.getString(R.string.daily_reminder_title))
                .setContentText(context.getString(R.string.daily_reminder_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Привет, моя любимая! 💕 Новая порция любви и сюрпризов ждет тебя в приложении. Не забудь зарядить батарейку настроения!"));

        // Show notification
        NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (notificationManager != null) {
            notificationManager.notify(1001, builder.build());
        }
    }
}