package com.niyaj.worker.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.utils.Constants.DAILY_SALARY_REMINDER_ID
import com.niyaj.common.utils.Constants.DAILY_SALARY_REMINDER_INTERVAL
import com.niyaj.common.utils.Constants.DAILY_SALARY_REMINDER_TIME_UNIT
import com.niyaj.data.mapper.toReminder
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.data.repository.ReminderRepository
import com.niyaj.model.DailySalaryReminder
import com.niyaj.notifications.Notifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

@HiltWorker
class DailySalaryReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workParams: WorkerParameters,
    private val reminderRepository: ReminderRepository,
    private val employeeRepository: EmployeeRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val notifier: Notifier,
) : CoroutineWorker(context, workParams) {
    private val currentTime = System.currentTimeMillis().toString()

    private val _salaryReminder = MutableStateFlow(DailySalaryReminder())

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        getDailySalaryReminderOnCurrentDate()

        if (!_salaryReminder.value.isCompleted) {
            if (currentTime in _salaryReminder.value.reminderStartTime.._salaryReminder.value.reminderEndTime) {
                notifier.showDailySalaryNotification(_salaryReminder.value.notificationId)
            }
            Result.failure()
        } else {
            notifier.stopDailySalaryNotification(_salaryReminder.value.notificationId)
            Result.success()
        }
    }


    private suspend fun getDailySalaryReminderOnCurrentDate() {
        withContext(ioDispatcher) {
            val doesEmployeeExist = employeeRepository.doesAnyEmployeeExist()

            if (doesEmployeeExist) {
                val reminder = reminderRepository.getDailySalaryReminder()

                if (reminder == null || _salaryReminder.value.reminderStartTime != reminder.reminderStartTime) {
                    withContext(ioDispatcher) {
                        reminderRepository.createOrUpdateReminder(_salaryReminder.value.toReminder())
                    }
                }

                _salaryReminder.value = reminderRepository.getDailySalaryReminder()!!
            }
        }
    }

    companion object {
        /**
         * Expedited periodic time work to generate report on app startup
         */
        fun dailySalaryWorker() = PeriodicWorkRequestBuilder<DelegatingWorker>(
            DAILY_SALARY_REMINDER_INTERVAL.toLong(),
            DAILY_SALARY_REMINDER_TIME_UNIT
        ).addTag(DAILY_SALARY_REMINDER_ID)
            .setInputData(DailySalaryReminderWorker::class.delegatedData())
            .build()

        val workerId = dailySalaryWorker().id
    }

}