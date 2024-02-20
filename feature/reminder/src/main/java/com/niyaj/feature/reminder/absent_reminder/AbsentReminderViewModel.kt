package com.niyaj.feature.reminder.absent_reminder

import android.text.format.DateUtils
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.getStartTime
import com.niyaj.data.mapper.toReminder
import com.niyaj.data.repository.AttendanceRepository
import com.niyaj.data.repository.ReminderRepository
import com.niyaj.model.AbsentReminder
import com.niyaj.model.Attendance
import com.niyaj.model.PaymentStatus.NotPaid
import com.niyaj.model.ReminderType
import com.niyaj.notifications.Notifier
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class AbsentReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val attendanceRepository: AttendanceRepository,
    private val notifier: Notifier,
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(getStartTime)
    val selectedDate = _selectedDate.asStateFlow()

    val employees = _selectedDate.flatMapLatest {
        reminderRepository.getReminderEmployee(it, ReminderType.Attendance)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _selectedEmployees = mutableStateListOf<String>()
    val selectedEmployees: SnapshotStateList<String> = _selectedEmployees

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var count: Int = 0

    fun onEvent(event: AbsentReminderEvent) {
        when (event) {
            is AbsentReminderEvent.SelectEmployee -> {
                if (_selectedEmployees.contains(event.employeeId)) {
                    _selectedEmployees.remove(event.employeeId)
                } else {
                    _selectedEmployees.add(event.employeeId)
                }
            }

            is AbsentReminderEvent.SelectAllEmployee -> {
                count += 1

                val employees =
                    employees.value.filter { it.paymentStatus == NotPaid }

                if (employees.isNotEmpty()) {
                    employees.forEach { employeeList ->
                        if (count % 2 != 0) {
                            val selectedEmployee =
                                _selectedEmployees.find { it == employeeList.employee.employeeId }

                            if (selectedEmployee == null) {
                                _selectedEmployees.add(employeeList.employee.employeeId)
                            }
                        } else {
                            _selectedEmployees.remove(employeeList.employee.employeeId)
                        }
                    }
                }
            }

            is AbsentReminderEvent.SelectDate -> {
                viewModelScope.launch {
                    _selectedDate.emit(event.date)
                }
            }

            is AbsentReminderEvent.MarkAsAbsent -> {
                viewModelScope.launch {
                    _selectedEmployees.forEach { employeeId ->
                        val employeeWithStatus =
                            employees.value.find { it.employee.employeeId == employeeId }

                        if (employeeWithStatus != null) {
                            val result = attendanceRepository.addOrUpdateAbsentEntry(
                                Attendance(
                                    employee = employeeWithStatus.employee,
                                    isAbsent = true,
                                    absentDate = _selectedDate.value.ifEmpty { getStartTime },
                                    createdAt = System.currentTimeMillis().toString()
                                ),
                                ""
                            )

                            when (result) {
                                is Resource.Success -> {
                                    _eventFlow.emit(UiEvent.Success("${employeeWithStatus.employee.employeeName} Marked as absent."))

                                }

                                is Resource.Error -> {
                                    _eventFlow.emit(
                                        UiEvent.Error(
                                            result.message
                                                ?: "Unable to mark ${employeeWithStatus.employee.employeeName} as absent."
                                        )
                                    )
                                }
                            }
                        } else {
                            _eventFlow.emit(UiEvent.Error("Unable to find employee"))
                        }
                    }

                    val markAsCompleted = DateUtils.isToday(_selectedDate.value.toLong())
                    val reminder = AbsentReminder(isCompleted = markAsCompleted).toReminder()

                    val result = reminderRepository.createOrUpdateReminder(reminder)

                    if (result) {
                        _eventFlow.emit(UiEvent.Success("Selected employee marked as absent on selected date."))
                        notifier.stopAttendanceNotification(reminder.notificationId)
                    }
                }
            }
        }
    }
}