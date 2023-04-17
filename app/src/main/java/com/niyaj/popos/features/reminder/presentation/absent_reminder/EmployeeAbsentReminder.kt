package com.niyaj.popos.features.reminder.presentation.absent_reminder

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.niyaj.popos.features.reminder.domain.model.AbsentReminder
import com.niyaj.popos.features.reminder.domain.use_cases.ReminderUseCases
import com.niyaj.popos.util.Constants.ABSENT_REMINDER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class EmployeeAbsentReminder @Inject constructor(
    private val reminderUseCases: ReminderUseCases,
    application: Application
) : ViewModel() {
    private val workManager = WorkManager.getInstance(application.applicationContext)

    private val _reminder = mutableStateOf(AbsentReminder())
    val reminder: State<AbsentReminder> = _reminder

    private val currentTime = System.currentTimeMillis().toString()

    val absentWorker = PeriodicWorkRequestBuilder<EmployeeAbsentReminderWorker>(
        _reminder.value.reminderInterval.toLong(),
        TimeUnit.valueOf(_reminder.value.reminderIntervalTimeUnit)
    ).addTag(ABSENT_REMINDER_ID).build()

    init {
        getAbsentReminderOnCurrentDate()
    }

    private fun getAbsentReminderOnCurrentDate() {
        viewModelScope.launch {
            val reminder = reminderUseCases.getAbsentReminder()

            if (reminder == null || reminder.reminderStartTime != _reminder.value.reminderStartTime) {
                withContext(Dispatchers.IO) {
                    reminderUseCases.createOrUpdateAbsentReminder(AbsentReminder())
                }

                _reminder.value = reminderUseCases.getAbsentReminder()!!
            }

            enqueueAbsentReminder()
        }
    }

    private fun enqueueAbsentReminder() {
        if (!_reminder.value.isCompleted) {
            if (currentTime in _reminder.value.reminderStartTime .. _reminder.value.reminderEndTime) {
                workManager.enqueueUniquePeriodicWork(
                    ABSENT_REMINDER_ID,
                    ExistingPeriodicWorkPolicy.KEEP,
                    absentWorker
                )
            } else {
                Timber.d("Absent Reminder is not right time")
            }
        }else {
            workManager.cancelWorkById(absentWorker.id)
        }
    }
}