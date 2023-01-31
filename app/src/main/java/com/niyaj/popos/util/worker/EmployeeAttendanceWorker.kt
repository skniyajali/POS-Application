package com.niyaj.popos.util.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.niyaj.popos.features.reminder.domain.use_cases.ReminderUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EmployeeAttendanceWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
    private val reminderUseCases: ReminderUseCases
) : CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        val reminder = reminderUseCases.getAbsentReminder()

        return if (reminder.isCompleted) Result.success() else Result.failure()
    }
}
