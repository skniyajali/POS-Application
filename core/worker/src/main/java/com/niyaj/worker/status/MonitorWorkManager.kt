package com.niyaj.worker.status

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.niyaj.common.utils.Constants.ABSENT_REMINDER_ID
import com.niyaj.common.utils.Constants.DAILY_SALARY_REMINDER_ID
import com.niyaj.worker.initializers.enqueueAttendanceWorker
import com.niyaj.worker.initializers.enqueueDailySalaryWorker
import com.niyaj.worker.initializers.enqueueDeletionWorker
import com.niyaj.worker.initializers.enqueueReportWorker
import com.niyaj.worker.workers.AttendanceReminderWorker
import com.niyaj.worker.workers.DELETE_DATA_WORKER_TAG
import com.niyaj.worker.workers.DailySalaryReminderWorker
import com.niyaj.worker.workers.GENERATE_REPORT_WORKER_TAG
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MonitorWorkManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : WorkMonitor {

    private val workManager = WorkManager.getInstance(context)

    override val isGeneratingReport: Flow<Boolean> = workManager
        .getWorkInfosForUniqueWorkFlow(GENERATE_REPORT_WORKER_TAG)
        .map(List<WorkInfo>::anyRunning)
        .conflate()


    override val isDeletingData: Flow<Boolean> = workManager
        .getWorkInfosForUniqueWorkFlow(DELETE_DATA_WORKER_TAG)
        .map(List<WorkInfo>::anyRunning)
        .conflate()

    override val isDailySalaryReminderRunning: Flow<Boolean> = workManager
        .getWorkInfosForUniqueWorkFlow(DAILY_SALARY_REMINDER_ID)
        .map(List<WorkInfo>::anyRunning)
        .conflate()

    override val isAttendanceReminderRunning: Flow<Boolean> = workManager
        .getWorkInfosForUniqueWorkFlow(ABSENT_REMINDER_ID)
        .map(List<WorkInfo>::anyRunning)
        .conflate()

    override fun requestGenerateReport() {
        workManager.enqueueReportWorker()
    }

    override fun requestDeletingData() {
        workManager.enqueueDeletionWorker()
    }

    override fun requestDailySalaryReminder() {
        workManager.enqueueDailySalaryWorker()
    }

    override fun requestAttendanceReminder() {
        workManager.enqueueAttendanceWorker()
    }

    override fun cancelDailySalaryReminder() {
        workManager.cancelWorkById(DailySalaryReminderWorker.workerId)
    }

    override fun cancelAttendanceReminder() {
        workManager.cancelWorkById(AttendanceReminderWorker.workerId)
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == WorkInfo.State.RUNNING }