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
import com.niyaj.popos.common.utils.Constants.ABSENT_REMINDER_ID
import com.niyaj.popos.common.utils.isOngoing
import com.niyaj.popos.features.MainActivity


/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.attendanceNotification(): Notification {
    ensureNotificationChannelExists()

    return NotificationCompat.Builder(this, ABSENT_REMINDER_ID)
        .setSmallIcon(R.drawable.baseline_calendar)
        .setContentTitle(getString(R.string.attendance_notification_title))
        .setContentText(getString(R.string.attendance_notification_text))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setContentIntent(this.createOrGetAttendancePendingIntent())
        .setAutoCancel(false)
        .setOngoing(isOngoing)
        .setOnlyAlertOnce(true)
        .build()
}

fun Context.createOrGetAttendancePendingIntent():PendingIntent {
    val attendanceReminderIntent = Intent(
        Intent.ACTION_VIEW,
        Constants.ABSENT_HOST_SECURE.toUri(),
        this,
        MainActivity::class.java
    )

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getActivity(
            /* context = */ this,
            /* requestCode = */ Constants.ABSENT_REMINDER_REQ_CODE,
            /* intent = */ attendanceReminderIntent,
            /* flags = */ PendingIntent.FLAG_IMMUTABLE
        )
    }else {
        PendingIntent.getActivity(
            /* context = */ this,
            /* requestCode = */ Constants.ABSENT_REMINDER_REQ_CODE,
            /* intent = */ attendanceReminderIntent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}

/**
 * Ensures that a notification channel is present if applicable
 */
private fun Context.ensureNotificationChannelExists() {
    val channel = NotificationChannel(
        ABSENT_REMINDER_ID,
        getString(R.string.attendance_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.attendance_notification_channel_name)
    }

    // Register the channel with the system
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}