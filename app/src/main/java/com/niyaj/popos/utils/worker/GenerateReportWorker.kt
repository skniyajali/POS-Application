package com.niyaj.popos.utils.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.niyaj.popos.R
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import com.niyaj.popos.utils.Constants
import com.niyaj.popos.utils.getEndTime
import com.niyaj.popos.utils.getStartTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random

@HiltWorker
class GenerateReportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
    private val reportsRepository: ReportsRepository
): CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext

        startForegroundService(context)
        val startDate = getStartTime
        val endDate = getEndTime

        val result = reportsRepository.generateReport(startDate, endDate)

        result.message?.let {
            Result.retry()
            val errorMessage = "Unable to generate report"
            return Result.failure(
                workDataOf(
                    WorkerKeys.ERROR_MSG to errorMessage
                )
            )
        }

        val successMessage = "Report Generated Successfully"
        return Result.success(
            workDataOf(
                WorkerKeys.GENERATE_REPORT to successMessage
            )
        )
    }

    private suspend fun startForegroundService(context: Context) {
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(context, Constants.GENERATE_REPORT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.generate_report_icon)
                    .setContentText("Sit relax while generating report")
                    .setContentTitle("Generating Report")
                    .build()
            )
        )
    }

}