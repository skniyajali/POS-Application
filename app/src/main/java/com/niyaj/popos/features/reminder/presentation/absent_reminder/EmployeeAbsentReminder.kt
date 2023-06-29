package com.niyaj.popos.features.reminder.presentation.absent_reminder

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.niyaj.popos.features.MainActivity
import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.features.reminder.domain.model.AbsentReminder
import com.niyaj.popos.features.reminder.domain.model.toReminder
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import com.niyaj.popos.utils.Constants.ABSENT_HOST
import com.niyaj.popos.utils.Constants.ABSENT_REMINDER_ID
import com.niyaj.popos.utils.showPendingIntentNotification
import com.niyaj.popos.utils.stopPendingIntentNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class EmployeeAbsentReminder @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val employeeRepository : EmployeeRepository,
    application: Application,
) : ViewModel() {
    private val workManager = WorkManager.getInstance(application.applicationContext)

    private val _reminder = mutableStateOf(AbsentReminder())
    val reminder: State<AbsentReminder> = _reminder

    private val doesEmployeeExist = employeeRepository.doesAnyEmployeeExist()

    private val currentTime = System.currentTimeMillis().toString()

    val absentWorker = PeriodicWorkRequestBuilder<EmployeeAbsentReminderWorker>(
        _reminder.value.reminderInterval.toLong(),
        TimeUnit.valueOf(_reminder.value.reminderIntervalTimeUnit)
    ).addTag(ABSENT_REMINDER_ID).build()

    private val absentReminderIntent = Intent(
        Intent.ACTION_VIEW,
        ABSENT_HOST.toUri(),
        application.applicationContext,
        MainActivity::class.java
    )

    private val pendingIntent: PendingIntent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        PendingIntent.getActivity(
            /* context = */ application.applicationContext,
            /* requestCode = */ 0,
            /* intent = */ absentReminderIntent,
            /* flags = */ PendingIntent.FLAG_IMMUTABLE
        )
    }else {
        PendingIntent.getActivity(
            /* context = */ application.applicationContext,
            /* requestCode = */ 0,
            /* intent = */ absentReminderIntent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    init {
        getAbsentReminderOnCurrentDate(application.applicationContext)
    }

    private fun getAbsentReminderOnCurrentDate(context : Context) {
        viewModelScope.launch {
            if (doesEmployeeExist) {
                val reminder = reminderRepository.getAbsentReminder()

                if (reminder == null || reminder.reminderStartTime != _reminder.value.reminderStartTime) {
                    withContext(Dispatchers.IO) {
                        reminderRepository.createOrUpdateReminder(AbsentReminder().toReminder())
                    }
                }

                _reminder.value = reminderRepository.getAbsentReminder()!!

                enqueueAbsentReminder(context)
            }
        }
    }

    private fun enqueueAbsentReminder(context : Context) {
        if (!_reminder.value.isCompleted) {
            if (currentTime in _reminder.value.reminderStartTime .. _reminder.value.reminderEndTime) {
                workManager.enqueueUniquePeriodicWork(
                    ABSENT_REMINDER_ID,
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    absentWorker
                )

                showPendingIntentNotification(context, _reminder.value.notificationId, pendingIntent)
            } else {
                Timber.d("Absent Reminder is not right time")
            }
        }else {
            workManager.cancelWorkById(absentWorker.id)
            stopPendingIntentNotification(context, _reminder.value.notificationId)
        }
    }

}