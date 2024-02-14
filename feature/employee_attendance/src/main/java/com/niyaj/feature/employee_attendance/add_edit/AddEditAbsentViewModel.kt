package com.niyaj.feature.employee_attendance.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.data.repository.AttendanceRepository
import com.niyaj.data.repository.validation.AttendanceValidationRepository
import com.niyaj.model.Attendance
import com.niyaj.model.Employee
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 *
 */
@HiltViewModel
class AddEditAbsentViewModel @Inject constructor(
    private val absentRepository: AttendanceRepository,
    private val validationRepository: AttendanceValidationRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val absentId = savedStateHandle.get<String>("attendanceId") ?: ""

    var state by mutableStateOf(AddEditAbsentState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val employees = snapshotFlow { absentId }.flatMapLatest {
        absentRepository.getAllEmployee()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _selectedEmployee = MutableStateFlow(Employee())
    val selectedEmployee = _selectedEmployee.asStateFlow()

    init {
        savedStateHandle.get<String>("attendanceId")?.let { absentId ->
            getAbsentById(absentId)
        }

        savedStateHandle.get<String>("employeeId")?.let { employeeId ->
            getEmployeeById(employeeId)
        }
    }

    val employeeError: StateFlow<String?> = _selectedEmployee
        .mapLatest {
            validationRepository.validateAbsentEmployee(it.employeeId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val observeDate = snapshotFlow { state.absentDate }

    val dateError = _selectedEmployee.combine(observeDate) { emp, date ->
        validationRepository.validateAbsentDate(
            absentDate = date,
            employeeId = emp.employeeId,
            attendanceId = absentId
        ).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun onEvent(event: AddEditAbsentEvent) {
        when (event) {

            is AddEditAbsentEvent.AbsentDateChanged -> {
                state = state.copy(absentDate = event.absentDate)
            }

            is AddEditAbsentEvent.AbsentReasonChanged -> {
                state = state.copy(absentReason = event.absentReason)
            }

            is AddEditAbsentEvent.CreateOrUpdateAbsent -> {
                createOrUpdateAbsent(absentId)
            }

            is AddEditAbsentEvent.OnSelectEmployee -> {
                viewModelScope.launch {
                    _selectedEmployee.value = event.employee
                }
            }
        }
    }

    private fun getAbsentById(itemId: String) {
        viewModelScope.launch {
            when (val result = absentRepository.getAttendanceById(itemId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error("Unable to find employee absent"))
                }

                is Resource.Success -> {
                    result.data?.let { absent ->
                        getEmployeeById(absent.employee?.employeeId ?: "")

                        state = state.copy(
                            absentDate = absent.absentDate,
                            absentReason = absent.absentReason
                        )
                    }
                }
            }
        }
    }

    private fun getEmployeeById(employeeId: String) {
        viewModelScope.launch {
            absentRepository.getEmployeeById(employeeId)?.let { employee ->
                _selectedEmployee.value = employee
            }
        }
    }

    private fun createOrUpdateAbsent(absentId: String = "") {
        viewModelScope.launch {
            val hasError = listOf(employeeError, dateError).all { it.value == null }

            if (hasError) {
                val newAbsent = Attendance(
                    attendeeId = absentId,
                    employee = _selectedEmployee.value,
                    absentDate = state.absentDate,
                    absentReason = state.absentReason.trim().capitalizeWords,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = if (absentId.isEmpty()) null else System.currentTimeMillis()
                        .toString()
                )

                if (absentId.isEmpty()) {
                    when (absentRepository.addAbsentEntry(newAbsent)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable To Mark Employee Absent."))
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Employee Marked As Absent."))
                        }
                    }
                } else {
                    when (absentRepository.updateAbsentEntry(newAbsent, absentId)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable To Mark Employee Absent."))
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Employee Marked As Absent."))
                        }
                    }
                }

                state = AddEditAbsentState()
            }
        }
    }
}