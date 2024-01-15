package com.niyaj.popos.notifications.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.niyaj.popos.R
import com.niyaj.popos.common.utils.Constants
import com.niyaj.popos.common.utils.Constants.DAILY_SALARY_REMINDER_ID
import com.niyaj.popos.common.utils.isOngoing
import com.niyaj.popos.features.MainActivity


/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.dailySalaryNotification(): Notification {
    ensureNotificationChannelExists()

    return NotificationCompat.Builder(this, DAILY_SALARY_REMINDER_ID)
        .setSmallIcon(R.drawable.baseline_account)
        .setContentTitle(getString(R.string.salary_notification_title))
        .setContentText(getString(R.string.salary_notification_text))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setContentIntent(this.createOrGetDailySalaryPendingIntent())
        .setAutoCancel(false)
        .setOngoing(isOngoing)
        .setOnlyAlertOnce(true)
        .build()
}


private fun Context.createOrGetDailySalaryPendingIntent():PendingIntent {
    val dailyReminderIntent = Intent(
        Intent.ACTION_VIEW,
        Constants.SALARY_HOST_SECURE.toUri(),
        this,
        MainActivity::class.java
    )

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getActivity(
            /* context = */ this,
            /* requestCode = */ Constants.DAILY_SALARY_REQ_CODE,
            /* intent = */ dailyReminderIntent,
            /* flags = */ PendingIntent.FLAG_IMMUTABLE
        )
    }else {
        PendingIntent.getActivity(
            /* context = */ this,
            /* requestCode = */ Constants.DAILY_SALARY_REQ_CODE,
            /* intent = */ dailyReminderIntent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}

/**
 * Ensures that a notification channel is present if applicable
 */
private fun Context.ensureNotificationChannelExists() {
    val channel = NotificationChannel(
        DAILY_SALARY_REMINDER_ID,
        getString(R.string.salary_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.salary_notification_channel_name)
    }

    // Register the channel with the system
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}