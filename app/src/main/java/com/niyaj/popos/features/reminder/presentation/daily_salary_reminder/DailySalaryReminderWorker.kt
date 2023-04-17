package com.niyaj.popos.features.reminder.presentation.daily_salary_reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.niyaj.popos.features.reminder.domain.use_cases.ReminderUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailySalaryReminderWorker @AssistedInject constructor(
    @Assisted context : Context,
    @Assisted workerParameters : WorkerParameters,
    private val reminderUseCases : ReminderUseCases
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork() : Result {
        val reminder = reminderUseCases.getDailySalaryReminder()

        return if (reminder != null && reminder.isCompleted) Result.success() else Result.failure()
    }
}