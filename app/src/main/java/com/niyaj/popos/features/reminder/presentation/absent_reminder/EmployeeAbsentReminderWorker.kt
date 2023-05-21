package com.niyaj.popos.features.reminder.presentation.absent_reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import com.niyaj.popos.utils.stopPendingIntentNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EmployeeAbsentReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
    private val reminderUseCases: ReminderRepository
) : CoroutineWorker(context, workParams) {

    val context = applicationContext

    override suspend fun doWork(): Result {
        val reminder = reminderUseCases.getAbsentReminder()

        return if (reminder != null && reminder.isCompleted) {
            stopPendingIntentNotification(context, reminder.notificationId)
            Result.success()
        } else Result.failure()
    }

}
