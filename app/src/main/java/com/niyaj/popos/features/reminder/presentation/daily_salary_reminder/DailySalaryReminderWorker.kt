package com.niyaj.popos.features.reminder.presentation.daily_salary_reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import com.niyaj.popos.utils.stopPendingIntentNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailySalaryReminderWorker @AssistedInject constructor(
    @Assisted context : Context,
    @Assisted workerParameters : WorkerParameters,
    private val reminderUseCases : ReminderRepository
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork() : Result {
        val reminder = reminderUseCases.getDailySalaryReminder()

        return if (reminder != null && reminder.isCompleted) {
            stopPendingIntentNotification(applicationContext, reminder.notificationId)

            Result.success()
        } else Result.failure()
    }
}