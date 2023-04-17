package com.niyaj.popos.features.reminder.presentation.daily_salary_reminder

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.niyaj.popos.features.reminder.domain.model.DailySalaryReminder
import com.niyaj.popos.features.reminder.domain.use_cases.ReminderUseCases
import com.niyaj.popos.features.reminder.presentation.absent_reminder.EmployeeAbsentReminderWorker
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
    private val reminderUseCases : ReminderUseCases,
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


    init {
        getDailySalaryReminderOnCurrentDate()
    }

    private fun getDailySalaryReminderOnCurrentDate() {
        viewModelScope.launch {
            val reminder = reminderUseCases.getDailySalaryReminder()

            if (reminder == null || _salaryReminder.value.reminderStartTime != reminder.reminderStartTime) {
                withContext(Dispatchers.IO) {
                    reminderUseCases.createOrUpdateDailySalaryReminder(_salaryReminder.value)
                }

                _salaryReminder.value = reminderUseCases.getDailySalaryReminder()!!
            }

            enqueueDailySalaryReminderWorker()
        }
    }

    private fun enqueueDailySalaryReminderWorker() {
        if (!_salaryReminder.value.isCompleted) {
            if (currentTime in _salaryReminder.value.reminderStartTime .. _salaryReminder.value.reminderEndTime) {
                workManager.enqueueUniquePeriodicWork(
                    _salaryReminder.value.reminderName,
                    ExistingPeriodicWorkPolicy.KEEP,
                    salaryWorker
                )
            }
        }else {
            workManager.cancelWorkById(salaryWorker.id)
        }
    }
}