package com.niyaj.popos.utils.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.niyaj.popos.R
import com.niyaj.popos.features.data_deletion.domain.repository.DataDeletionRepository
import com.niyaj.popos.utils.Constants.DELETE_DATA_NOTIFICATION_CHANNEL_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random

@HiltWorker
class DataDeletionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
    private val deletionRepository: DataDeletionRepository
): CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext

        startForegroundService(context)

        val result = deletionRepository.deleteData()

        result.message?.let { message ->
            Result.retry()

            return Result.failure(
                workDataOf(
                    WorkerKeys.ERROR_MSG to message
                )
            )
        }

        val successMessage = "Old data deleted successfully"
        return Result.success(
            workDataOf(
                WorkerKeys.DELETE_DATA to successMessage
            )
        )
    }

    private suspend fun startForegroundService(context: Context) {
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                NotificationCompat.Builder(context, DELETE_DATA_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.auto_delete_icon)
                    .setContentText("Data Deletion")
                    .setContentTitle("Deletion in progress")
                    .build()
            )
        )
    }
}