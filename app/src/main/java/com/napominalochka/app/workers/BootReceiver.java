package com.napominalochka.app.workers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.napominalochka.app.utils.NotificationScheduler;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Boot receiver triggered with action: " + action);

        if (Intent.ACTION_BOOT_COMPLETED.equals(action) ||
                "android.intent.action.QUICKBOOT_POWERON".equals(action)) {

            // Перепланируем уведомления после перезагрузки устройства
            Log.d(TAG, "Device booted, rescheduling notifications");
            NotificationScheduler.scheduleDailyNotifications(context);
        }
    }
}