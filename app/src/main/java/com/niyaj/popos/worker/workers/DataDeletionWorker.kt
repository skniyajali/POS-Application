package com.niyaj.popos.worker.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.niyaj.popos.common.network.Dispatcher
import com.niyaj.popos.common.network.PoposDispatchers
import com.niyaj.popos.features.data_deletion.domain.repository.DataDeletionRepository
import com.niyaj.popos.notifications.Notifier
import com.niyaj.popos.notifications.utils.DELETION_NOTIFICATION_ID
import com.niyaj.popos.notifications.utils.deletionWorkNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

const val DELETE_DATA_WORKER_TAG = "Data Deletion Worker"
const val DELETE_DATA_INTERVAL_HOUR: Long = 15


@HiltWorker
class DataDeletionWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workParams: WorkerParameters,
    private val deletionRepository: DataDeletionRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val notifier: Notifier,
) : CoroutineWorker(context, workParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        context.deletionForegroundInfo()


    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        // Send the notifications
        notifier.showDataDeletionNotification()

        val result = deletionRepository.deleteData()
        result.message?.let {
            Result.retry()

            Result.failure()
        }

        Result.success()
    }


    companion object {
        /**
         * Expedited periodic time work to generate report on app startup
         */
        fun deletionWorker() = PeriodicWorkRequestBuilder<DelegatingWorker>(
            DELETE_DATA_INTERVAL_HOUR,
            TimeUnit.DAYS
        ).addTag(DELETE_DATA_WORKER_TAG)
            .setInputData(DataDeletionWorker::class.delegatedData())
            .build()
    }
}

/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.deletionForegroundInfo() = ForegroundInfo(
    DELETION_NOTIFICATION_ID,
    deletionWorkNotification(),
)