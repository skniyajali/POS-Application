package com.niyaj.popos.features.reminder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val attendanceRepository : AttendanceRepository,
    private val salaryRepository : SalaryRepository
): ViewModel() {

    private val _state = MutableStateFlow(ReminderState())
    val state = _state.asStateFlow()

    private val _selectedReminder = MutableStateFlow("")
    val selectedReminder = _selectedReminder.asStateFlow()

    private var reminderJob: Job? = null

    init {
        getAllReminders()
    }

    fun onEvent(event : ReminderEvent) {
        when(event) {
            is ReminderEvent.SelectReminder -> {
                if (_selectedReminder.value == event.reminderId) {
                    _selectedReminder.value = ""
                }else {
                    _selectedReminder.value = event.reminderId
                }
            }

            is ReminderEvent.DeselectReminder -> {
                _selectedReminder.value = ""
            }

            is ReminderEvent.UpdateReminder -> {
                viewModelScope.launch {
                    reminderRepository.updateReminderAsNotCompleted(event.reminderId)
                }
            }

            is ReminderEvent.DeleteReminder -> {
                viewModelScope.launch {
                    reminderRepository.deleteReminder(event.reminderId)
                }
            }

            is ReminderEvent.RefreshReminder -> {
                getAllReminders()
            }

        }
    }

    private fun getAllReminders() {
        reminderJob?.cancel()

        reminderJob = reminderRepository.getAllReminders().onEach { result ->
            when(result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        isLoading = result.isLoading
                    )
                }
                is Resource.Success -> {
                    result.data?.let {
                        _state.value = _state.value.copy(
                            reminders = it
                        )
                    }
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        hasErrors = result.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

}