package com.niyaj.popos.worker.status

import kotlinx.coroutines.flow.Flow

/**
 * Reports on if worker is in progress
 */
interface WorkMonitor {

    val isGeneratingReport: Flow<Boolean>

    val isDeletingData: Flow<Boolean>

    val isDailySalaryReminderRunning: Flow<Boolean>

    val isAttendanceReminderRunning: Flow<Boolean>

    fun requestGenerateReport()

    fun requestDeletingData()

    fun requestDailySalaryReminder()

    fun requestAttendanceReminder()

    fun cancelDailySalaryReminder()

    fun cancelAttendanceReminder()
}
