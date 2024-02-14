package com.niyaj.feature.employee.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.data.repository.validation.EmployeeValidationRepository
import com.niyaj.model.Employee
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Niyaj on 9/10/2020.
 */
@HiltViewModel
class AddEditEmployeeViewModel @Inject constructor(
    private val repository: EmployeeRepository,
    private val validationRepository: EmployeeValidationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var employeeId = savedStateHandle.get<String>("employeeId") ?: ""

    var state by mutableStateOf(AddEditEmployeeState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("employeeId")?.let { employeeId ->
            getEmployeeById(employeeId)
        }
    }

    val phoneError: StateFlow<String?> = snapshotFlow { state.employeePhone }
        .mapLatest {
            validationRepository.validateEmployeePhone(it, employeeId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val nameError: StateFlow<String?> = snapshotFlow { state.employeeName }
        .mapLatest {
            validationRepository.validateEmployeeName(it, employeeId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val salaryError: StateFlow<String?> = snapshotFlow { state.employeeSalary }
        .mapLatest {
            validationRepository.validateEmployeeSalary(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val positionError: StateFlow<String?> = snapshotFlow { state.employeePosition }
        .mapLatest {
            validationRepository.validateEmployeePosition(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onEvent(event: AddEditEmployeeEvent) {
        when (event) {
            is AddEditEmployeeEvent.EmployeeNameChanged -> {
                state = state.copy(employeeName = event.employeeName)
            }

            is AddEditEmployeeEvent.EmployeePhoneChanged -> {
                state = state.copy(employeePhone = event.employeePhone)

            }

            is AddEditEmployeeEvent.EmployeeSalaryChanged -> {
                state = state.copy(employeeSalary = event.employeeSalary)
            }

            is AddEditEmployeeEvent.EmployeeJoinedDateChanged -> {
                state = state.copy(employeeJoinedDate = event.employeeJoinedDate)
            }

            is AddEditEmployeeEvent.EmployeePositionChanged -> {
                state = state.copy(employeePosition = event.employeePosition)
            }

            is AddEditEmployeeEvent.EmployeeSalaryTypeChanged -> {
                state = state.copy(employeeSalaryType = event.employeeSalaryType)
            }

            is AddEditEmployeeEvent.EmployeeTypeChanged -> {
                state = state.copy(employeeType = event.employeeType)
            }

            is AddEditEmployeeEvent.CreateOrUpdateEmployee -> {
                createOrUpdateEmployee(employeeId)
            }
        }
    }

    private fun getEmployeeById(itemId: String) {
        viewModelScope.launch {
            repository.getEmployeeById(itemId)?.let { employee ->
                state = state.copy(
                    employeePhone = employee.employeePhone,
                    employeeName = employee.employeeName,
                    employeeSalary = employee.employeeSalary,
                    employeePosition = employee.employeePosition,
                    employeeSalaryType = employee.employeeSalaryType,
                    employeeType = employee.employeeType,
                    employeeJoinedDate = employee.employeeJoinedDate
                )
            }
        }
    }

    private fun createOrUpdateEmployee(employeeId: String = "") {
        viewModelScope.launch {
            val hasError = listOf(phoneError, nameError, salaryError, positionError).all {
                it.value != null
            }

            if (!hasError) {
                val newEmployee = Employee(
                    employeeId = employeeId,
                    employeeName = state.employeeName.trim().capitalizeWords,
                    employeePhone = state.employeePhone.trim(),
                    employeeSalary = state.employeeSalary.trim(),
                    employeePosition = state.employeePosition,
                    employeeSalaryType = state.employeeSalaryType,
                    employeeType = state.employeeType,
                    employeeJoinedDate = state.employeeJoinedDate,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = if (employeeId.isEmpty()) System.currentTimeMillis()
                        .toString() else null
                )

                if (employeeId.isEmpty()) {
                    when (repository.createNewEmployee(newEmployee)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable To Create Employee."))
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Employee Created Successfully."))
                        }
                    }
                } else {
                    when (repository.updateEmployee(newEmployee, employeeId)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable To Create Employee."))
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Employee Updated Successfully."))
                        }
                    }
                }

                state = AddEditEmployeeState()
            }
        }
    }

}