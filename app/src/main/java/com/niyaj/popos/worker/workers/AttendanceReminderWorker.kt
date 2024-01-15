package com.niyaj.popos.worker.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.niyaj.popos.common.network.Dispatcher
import com.niyaj.popos.common.network.PoposDispatchers
import com.niyaj.popos.common.utils.Constants.ABSENT_REMINDER_ID
import com.niyaj.popos.common.utils.Constants.ABSENT_REMINDER_INTERVAL
import com.niyaj.popos.common.utils.Constants.ABSENT_REMINDER_TIME_UNIT
import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.features.reminder.domain.model.AbsentReminder
import com.niyaj.popos.features.reminder.domain.model.toReminder
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import com.niyaj.popos.notifications.Notifier
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

@HiltWorker
class AttendanceReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workParams: WorkerParameters,
    private val reminderRepository: ReminderRepository,
    private val employeeRepository: EmployeeRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val notifier: Notifier,
) : CoroutineWorker(context, workParams) {
    private val currentTime = System.currentTimeMillis().toString()

    private val _attendanceReminder = MutableStateFlow(AbsentReminder())

    override suspend fun doWork(): Result = withContext(ioDispatcher) {

        getAttendanceReminderOnCurrentDate()

        if (!_attendanceReminder.value.isCompleted) {
            if (currentTime in _attendanceReminder.value.reminderStartTime.._attendanceReminder.value.reminderEndTime) {
                notifier.showAttendanceNotification(_attendanceReminder.value.notificationId)
            } else {
                notifier.stopAttendanceNotification(_attendanceReminder.value.notificationId)
                Result.success()
            }

            Result.failure()
        } else {
            notifier.stopAttendanceNotification(_attendanceReminder.value.notificationId)
            Result.failure()
        }
    }

    private suspend fun getAttendanceReminderOnCurrentDate() {
        withContext(ioDispatcher) {
            val doesEmployeeExist = employeeRepository.doesAnyEmployeeExist()

            if (doesEmployeeExist) {
                val reminder = reminderRepository.getAbsentReminder()

                if (reminder == null || _attendanceReminder.value.reminderStartTime != reminder.reminderStartTime) {
                    withContext(ioDispatcher) {
                        reminderRepository.createOrUpdateReminder(_attendanceReminder.value.toReminder())
                    }
                }

                _attendanceReminder.value = reminderRepository.getAbsentReminder()!!
            }
        }
    }

    companion object {
        /**
         * Expedited periodic time work to generate report on app startup
         */
        fun attendanceWorker() = PeriodicWorkRequestBuilder<DelegatingWorker>(
            ABSENT_REMINDER_INTERVAL.toLong(),
            ABSENT_REMINDER_TIME_UNIT
        ).addTag(ABSENT_REMINDER_ID)
            .setInputData(AttendanceReminderWorker::class.delegatedData())
            .build()

        val workerId = attendanceWorker().id
    }

}