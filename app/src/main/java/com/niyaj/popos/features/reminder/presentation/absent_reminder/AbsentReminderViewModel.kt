package com.niyaj.popos.features.reminder.presentation.absent_reminder

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.employee.domain.use_cases.EmployeeUseCases
import com.niyaj.popos.features.employee.presentation.EmployeeState
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_attendance.domain.use_cases.AttendanceUseCases
import com.niyaj.popos.features.reminder.domain.model.AbsentReminder
import com.niyaj.popos.features.reminder.domain.use_cases.ReminderUseCases
import com.niyaj.popos.util.getStartTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AbsentReminderViewModel @Inject constructor(
    private val reminderUseCases: ReminderUseCases,
    private val employeeUseCases : EmployeeUseCases,
    private val attendanceUseCases : AttendanceUseCases,
    application: Application,
) : ViewModel() {
    val workManager: WorkManager = WorkManager.getInstance(application.applicationContext)

    private val _reminder = mutableStateOf(AbsentReminder())
    val reminder: State<AbsentReminder> = _reminder

    private val _state = MutableStateFlow(EmployeeState())
    val state = _state.asStateFlow()

    private val _selectedEmployees = mutableStateListOf<String>()
    val selectedEmployees: SnapshotStateList<String> = _selectedEmployees

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var count: Int = 0

    init {
        getAbsentReminder()
        getAllEmployees()
    }


    fun onEvent(event : AbsentReminderEvent) {
        when(event) {
            is AbsentReminderEvent.SelectEmployee -> {
                if (_selectedEmployees.contains(event.employeeId)) {
                    _selectedEmployees.remove(event.employeeId)
                }else {
                    _selectedEmployees.add(event.employeeId)
                }
            }

            is AbsentReminderEvent.SelectAllEmployee -> {
                count += 1

                val employees = _state.value.employees

                if (employees.isNotEmpty()){
                    employees.forEach { employee ->
                        if (count % 2 != 0){
                            val selectedEmployee = _selectedEmployees.find { it == employee.employeeId }

                            if (selectedEmployee == null){
                                _selectedEmployees.add(employee.employeeId)
                            }
                        }else {
                            _selectedEmployees.remove(employee.employeeId)
                        }
                    }
                }
            }

            is AbsentReminderEvent.MarkAbsent -> {
                viewModelScope.launch {
                    _selectedEmployees.forEach { employeeId ->
                        val employee = _state.value.employees.find { it.employeeId == employeeId }

                        if (employee != null) {
                            val result = attendanceUseCases.addAbsentEntry(
                                EmployeeAttendance(
                                    employee = employee,
                                    isAbsent = true,
                                    absentDate = getStartTime,
                                )
                            )

                            when(result) {
                                is Resource.Loading -> {
                                    _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                                }
                                is Resource.Success -> {
                                    _eventFlow.emit(UiEvent.OnSuccess("${employee.employeeName} Marked as absent on today."))
                                }
                                is Resource.Error -> {
                                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to mark ${employee.employeeName} as absent on today."))
                                }
                            }
                        } else {
                            _eventFlow.emit(UiEvent.OnError("Unable to find employee"))
                        }
                    }

                    val result = reminderUseCases.createOrUpdateAbsentReminder(AbsentReminder(isCompleted = true))

                    if (result) {
                        _eventFlow.emit(UiEvent.OnSuccess("Selected employee marked as absent on today."))
                    }
                }
            }
        }
    }

    private fun getAbsentReminder() {
        viewModelScope.launch {
            _reminder.value = reminderUseCases.getAbsentReminder()
        }
    }

    private fun getAllEmployees() {
        viewModelScope.launch {
            employeeUseCases.getAllEmployee().collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _state.value = _state.value.copy(
                                employees = it,
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(error = result.message)
                    }
                }
            }
        }
    }

}
