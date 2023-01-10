package com.niyaj.popos.features.employee_attendance.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.use_cases.EmployeeUseCases
import com.niyaj.popos.features.employee.domain.util.FilterEmployee
import com.niyaj.popos.features.employee.presentation.EmployeeState
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_attendance.domain.use_cases.AttendanceUseCases
import com.niyaj.popos.features.employee_attendance.domain.use_cases.validation.ValidateAbsentDate
import com.niyaj.popos.features.employee_attendance.domain.use_cases.validation.ValidateAbsentEmployee
import com.niyaj.popos.features.employee_attendance.domain.use_cases.validation.ValidateIsAbsent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AbsentViewModel @Inject constructor(
    private val validateAbsentEmployee: ValidateAbsentEmployee,
    private val validateIsAbsent: ValidateIsAbsent,
    private val validateAbsentDate: ValidateAbsentDate,
    private val employeeUseCases: EmployeeUseCases,
    private val attendanceUseCases: AttendanceUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _employeeState = MutableStateFlow(EmployeeState())
    val employeeState = _employeeState.asStateFlow()

    var absentState by mutableStateOf(AbsentState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("employeeId")?.let { employeeId ->
            getEmployeeById(employeeId)
        }

        savedStateHandle.get<String>("attendanceId")?.let { attendanceId ->
            getAttendanceById(attendanceId)
        }

        getAllEmployees(filterEmployee = FilterEmployee.ByEmployeeId(SortType.Descending))
    }

    fun onEvent(event: AbsentEvent) {
        when (event) {
            is AbsentEvent.EmployeeChanged -> {
                getEmployeeById(event.employeeId)
            }

            is AbsentEvent.AbsentChanged -> {
                absentState = absentState.copy(
                    isAbsent = event.isAbsent
                )
            }

            is AbsentEvent.AbsentDateChanged -> {
                absentState = absentState.copy(
                    absentDate = event.absentDate
                )
            }

            is AbsentEvent.AbsentReasonChanged -> {
                absentState = absentState.copy(
                    absentReason = event.absentReason
                )
            }

            is AbsentEvent.AddAbsentEntry -> {
                addUpdateAbsentEntry()
            }

            is AbsentEvent.UpdateAbsentEntry -> {
                addUpdateAbsentEntry(event.attendanceId)
            }

        }
    }

    private fun addUpdateAbsentEntry(attendanceId: String = "") {

        val validatedEmployee = validateAbsentEmployee.validate(absentState.employee.employeeId)
        val validateIsAbsent = validateIsAbsent.validate(absentState.isAbsent)
        val validateAbsentDate = validateAbsentDate.validate(
            absentState.absentDate,
            absentState.employee.employeeId,
            attendanceId
        )

        val hasError = listOf(validatedEmployee, validateIsAbsent, validateAbsentDate).any {
            !it.successful
        }

        if (hasError) {

            absentState = absentState.copy(
                employeeError = validatedEmployee.errorMessage,
                isAbsentError = validateIsAbsent.errorMessage,
                absentDateError = validateAbsentDate.errorMessage
            )

            return
        } else {
            viewModelScope.launch {
                if (attendanceId.isEmpty()) {

                    val result = attendanceUseCases.addAbsentEntry(
                        EmployeeAttendance(
                            employee = absentState.employee,
                            isAbsent = absentState.isAbsent,
                            absentReason = absentState.absentReason,
                            absentDate = absentState.absentDate,
                        )
                    )

                    when (result) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Employee absent entry has been added."))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(
                                UiEvent.OnError(result.message
                                ?: "Unable to add absent entry."))
                        }
                    }

                } else {
                    val result = attendanceUseCases.updateAbsentEntry(
                        attendanceId,
                        EmployeeAttendance(
                            employee = absentState.employee,
                            isAbsent = absentState.isAbsent,
                            absentReason = absentState.absentReason,
                            absentDate = absentState.absentDate,
                        )
                    )

                    when (result) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Employee absent entry has been updated."))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(
                                UiEvent.OnError(result.message
                                ?: "Unable to update absent entry."))
                        }
                    }
                }
            }
        }
    }

    private fun getAllEmployees(filterEmployee: FilterEmployee, searchText: String = "") {
        viewModelScope.launch {
            employeeUseCases.getAllEmployee(filterEmployee, searchText).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _employeeState.value = _employeeState.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _employeeState.value = _employeeState.value.copy(
                                employees = it,
                                filterEmployee = filterEmployee
                            )
                        }
                    }
                    is Resource.Error -> {
                        _employeeState.value = _employeeState.value.copy(error = "Unable to load resources")
                        _eventFlow.emit(
                            UiEvent.OnError(result.message
                            ?: "Unable to load resources"))
                    }
                }
            }
        }
    }

    private fun getEmployeeById(employeeId: String) {
        if (employeeId.isNotEmpty()) {
            viewModelScope.launch {
                when (val result = employeeUseCases.getEmployeeById(employeeId)) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        result.data?.let {
                            absentState = absentState.copy(
                                employee = Employee(
                                    employeeId = it.employeeId,
                                    employeeName = it.employeeName,
                                    employeePhone = it.employeePhone,
                                    employeeSalary = it.employeeSalary,
                                    employeeSalaryType = it.employeeSalaryType,
                                    employeePosition = it.employeePosition,
                                    employeeType = it.employeeType,
                                    employeeJoinedDate = it.employeeJoinedDate
                                )
                            )
                        }
                    }
                    is Resource.Error -> {}
                }
            }
        }
    }

    private fun getAttendanceById(attendanceId: String) {
        viewModelScope.launch {
            val result = attendanceUseCases.getAttendanceById(attendanceId)

            result.data?.let { attendance ->
                if (attendance.employee != null){
                    absentState = absentState.copy(
                        employee = attendance.employee!!,
                        isAbsent = attendance.isAbsent,
                        absentDate = attendance.absentDate,
                        absentReason = attendance.absentReason
                    )
                }
            } ?: _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to find absent report"))
        }
    }
}