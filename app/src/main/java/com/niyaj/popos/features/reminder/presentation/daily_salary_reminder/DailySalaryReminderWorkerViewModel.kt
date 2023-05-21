package com.niyaj.popos.features.reminder.presentation.daily_salary_reminder

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.niyaj.popos.R
import com.niyaj.popos.features.MainActivity
import com.niyaj.popos.features.reminder.domain.model.DailySalaryReminder
import com.niyaj.popos.features.reminder.domain.model.toReminder
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import com.niyaj.popos.features.reminder.presentation.absent_reminder.EmployeeAbsentReminderWorker
import com.niyaj.popos.utils.Constants.DAILY_SALARY_REMINDER_ID
import com.niyaj.popos.utils.Constants.DAILY_SALARY_REMINDER_NAME
import com.niyaj.popos.utils.Constants.DAILY_SALARY_REMINDER_TEXT
import com.niyaj.popos.utils.showPendingIntentNotification
import com.niyaj.popos.utils.stopPendingIntentNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DailySalaryReminderWorkerViewModel @Inject constructor(
    private val reminderRepository : ReminderRepository,
    application: Application,
): ViewModel() {
    private val workManager = WorkManager.getInstance(application.applicationContext)
    private val currentTime = System.currentTimeMillis().toString()

    private val _salaryReminder = MutableStateFlow(DailySalaryReminder())
    val salaryReminder = _salaryReminder.asStateFlow()

    val salaryWorker = PeriodicWorkRequestBuilder<EmployeeAbsentReminderWorker>(
        _salaryReminder.value.reminderInterval.toLong(),
        TimeUnit.valueOf(_salaryReminder.value.reminderIntervalTimeUnit)
    ).addTag(_salaryReminder.value.dailySalaryRemId).build()

    private val dailyReminderIntent = Intent(
        Intent.ACTION_VIEW,
        "https://popos.com/reminder/reminder_id=${DAILY_SALARY_REMINDER_ID}".toUri(),
        application.applicationContext,
        MainActivity::class.java
    )

    private val pendingIntent: PendingIntent = PendingIntent.getActivity(
        /* context = */ application.applicationContext,
        /* requestCode = */ 0,
        /* intent = */ dailyReminderIntent,
        /* flags = */ PendingIntent.FLAG_IMMUTABLE
    )

    init {
        getDailySalaryReminderOnCurrentDate(application.applicationContext)
    }

    private fun getDailySalaryReminderOnCurrentDate(context : Context) {
        viewModelScope.launch {
            val reminder = reminderRepository.getDailySalaryReminder()

            if (reminder == null || _salaryReminder.value.reminderStartTime != reminder.reminderStartTime) {
                withContext(Dispatchers.IO) {
                    reminderRepository.createOrUpdateReminder(_salaryReminder.value.toReminder())
                }
            }

            _salaryReminder.value = reminderRepository.getDailySalaryReminder()!!

            enqueueDailySalaryReminderWorker(context)
        }
    }

    private fun enqueueDailySalaryReminderWorker(context: Context) {
        if (!_salaryReminder.value.isCompleted) {
            if (currentTime in _salaryReminder.value.reminderStartTime .. _salaryReminder.value.reminderEndTime) {
                workManager.enqueueUniquePeriodicWork(
                    _salaryReminder.value.reminderName,
                    ExistingPeriodicWorkPolicy.KEEP,
                    salaryWorker
                )

                showPendingIntentNotification(
                    context = context,
                    notificationId = _salaryReminder.value.notificationId,
                    pendingIntent = pendingIntent,
                    channelId = DAILY_SALARY_REMINDER_ID,
                    title = DAILY_SALARY_REMINDER_NAME,
                    text = DAILY_SALARY_REMINDER_TEXT,
                    icon = R.drawable.baseline_account
                )
            }
        }else {
            workManager.cancelWorkById(salaryWorker.id)
            stopPendingIntentNotification(context, _salaryReminder.value.notificationId)
        }
    }
}