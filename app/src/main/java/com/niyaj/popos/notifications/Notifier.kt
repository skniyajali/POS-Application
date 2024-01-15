package com.niyaj.popos.notifications

interface Notifier {
    fun showDataDeletionNotification()

    fun showReportGenerationNotification()

    fun showDailySalaryNotification(notificationId: Int)

    fun stopDailySalaryNotification(notificationId:Int)

    fun showAttendanceNotification(notificationId: Int)

    fun stopAttendanceNotification(notificationId:Int)
}