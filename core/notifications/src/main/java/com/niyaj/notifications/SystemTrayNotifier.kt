package com.niyaj.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.niyaj.notifications.utils.DELETION_NOTIFICATION_ID
import com.niyaj.notifications.utils.REPORT_NOTIFICATION_ID
import com.niyaj.notifications.utils.attendanceNotification
import com.niyaj.notifications.utils.dailySalaryNotification
import com.niyaj.notifications.utils.deletionWorkNotification
import com.niyaj.notifications.utils.reportWorkNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [Notifier] that displays notifications in the system tray.
 */
@Singleton
class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {

    override fun showDataDeletionNotification() = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(DELETION_NOTIFICATION_ID, deletionWorkNotification())
    }

    override fun showReportGenerationNotification() = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(REPORT_NOTIFICATION_ID, reportWorkNotification())
    }

    override fun showDailySalaryNotification(notificationId: Int) = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, dailySalaryNotification())
    }

    override fun stopDailySalaryNotification(notificationId: Int) = with(context) {
        NotificationManagerCompat.from(this).cancel(notificationId)
    }

    override fun showAttendanceNotification(notificationId: Int) = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, attendanceNotification())
    }

    override fun stopAttendanceNotification(notificationId: Int) = with(context) {
        NotificationManagerCompat.from(this).cancel(notificationId)
    }
}