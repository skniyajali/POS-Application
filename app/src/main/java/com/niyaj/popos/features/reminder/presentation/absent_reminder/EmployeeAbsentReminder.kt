package com.niyaj.popos.features.reminder.presentation.absent_reminder

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.niyaj.popos.features.reminder.domain.model.ABSENT_REMINDER_ID
import com.niyaj.popos.features.reminder.domain.model.AbsentReminder
import com.niyaj.popos.features.reminder.domain.use_cases.ReminderUseCases
import com.niyaj.popos.util.closingTime
import com.niyaj.popos.util.getStartTime
import com.niyaj.popos.util.openingTime
import com.niyaj.popos.util.worker.EmployeeAttendanceWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class EmployeeAbsentReminder @Inject constructor(
    private val reminderUseCases: ReminderUseCases,
    application: Application
) : ViewModel() {
    private val workManager: WorkManager = WorkManager.getInstance(application.applicationContext)

    private val _reminder = mutableStateOf(AbsentReminder())
    val reminder : State<AbsentReminder> = _reminder

    private val currentTime = System.currentTimeMillis().toString()

    init {
        getAttendanceReminder()
    }

    val attendanceWorkRequest =
        PeriodicWorkRequestBuilder<EmployeeAttendanceWorker>(
            _reminder.value.reminderInterval.toLong(),
            TimeUnit.valueOf(_reminder.value.reminderIntervalTimeUnit)
        ).addTag(ABSENT_REMINDER_ID).setInitialDelay(500L, TimeUnit.MILLISECONDS).build()

    private fun getAttendanceReminder() {
        viewModelScope.launch {
            _reminder.value = reminderUseCases.getAbsentReminder()

            if (!_reminder.value.isCompleted && _reminder.value.reminderStartTime != getStartTime) {
                reminderUseCases.createOrUpdateAbsentReminder(AbsentReminder())
            }
        }
    }


    init {
        if (!_reminder.value.isCompleted) {
            if (currentTime in openingTime..closingTime) {
                workManager.enqueueUniquePeriodicWork(
                    _reminder.value.reminderName,
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    attendanceWorkRequest
                )
            }else {
                Timber.d("Not In Correct Time..")
            }
        }else {
            workManager.cancelAllWorkByTag(ABSENT_REMINDER_ID)
        }
    }
}