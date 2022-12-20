package com.niyaj.popos.features.employee.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.use_cases.EmployeeUseCases
import com.niyaj.popos.features.employee.domain.use_cases.validation.ValidateEmployeeName
import com.niyaj.popos.features.employee.domain.use_cases.validation.ValidateEmployeePhone
import com.niyaj.popos.features.employee.domain.use_cases.validation.ValidateEmployeePosition
import com.niyaj.popos.features.employee.domain.use_cases.validation.ValidateEmployeeSalary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditEmployeeViewModel @Inject constructor(
    private val validateEmployeeName: ValidateEmployeeName,
    private val validateEmployeePhone: ValidateEmployeePhone,
    private val validateEmployeeSalary: ValidateEmployeeSalary,
    private val validateEmployeePosition: ValidateEmployeePosition,
    private val employeeUseCases: EmployeeUseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var addEditState by mutableStateOf(AddEditEmployeeState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var expanded by mutableStateOf(false)


    init {
        savedStateHandle.get<String>("employeeId")?.let { employeeId ->
            getEmployeeById(employeeId)
        }
    }

    fun onAddEditEmployeeEvent(event: AddEditEmployeeEvent) {
        when (event){

            is AddEditEmployeeEvent.EmployeeNameChanged -> {
                addEditState = addEditState.copy(employeeName = event.employeeName)
            }

            is AddEditEmployeeEvent.EmployeePhoneChanged -> {
                addEditState = addEditState.copy(employeePhone = event.employeePhone)
            }

            is AddEditEmployeeEvent.EmployeeSalaryChanged -> {
                addEditState = addEditState.copy(employeeSalary = event.employeeSalary)
            }

            is AddEditEmployeeEvent.EmployeeJoinedDateChanged -> {
                addEditState = addEditState.copy(employeeJoinedDate = event.employeeJoinedDate)
            }
            is AddEditEmployeeEvent.EmployeePositionChanged -> {
                addEditState = addEditState.copy(employeePosition = event.employeePosition)

            }
            is AddEditEmployeeEvent.EmployeeSalaryTypeChanged -> {
                addEditState = addEditState.copy(employeeSalaryType = event.employeeSalaryType)

            }
            is AddEditEmployeeEvent.EmployeeTypeChanged -> {
                addEditState = addEditState.copy(employeeType = event.employeeType)
            }

            is AddEditEmployeeEvent.CreateNewEmployee -> {
                addOrEditEmployee()
            }

            is AddEditEmployeeEvent.UpdateEmployee -> {
                addOrEditEmployee(event.employeeId)
            }
        }
    }

    private fun addOrEditEmployee(employeeId: String? = null){
        val validatedEmployeeName = validateEmployeeName.execute(addEditState.employeeName, employeeId)
        val validatedEmployeePhone = validateEmployeePhone.execute(addEditState.employeePhone, employeeId)
        val validatedEmployeeSalary = validateEmployeeSalary.execute(addEditState.employeeSalary)
        val validatedEmployeePosition = validateEmployeePosition.execute(addEditState.employeePosition)

        val hasError = listOf(validatedEmployeeName, validatedEmployeePhone, validatedEmployeeSalary, validatedEmployeePosition).any {
            !it.successful
        }

        if (hasError) {
            addEditState = addEditState.copy(
                employeeNameError = validatedEmployeeName.errorMessage,
                employeePhoneError = validatedEmployeePhone.errorMessage,
                employeeSalaryError = validatedEmployeeSalary.errorMessage,
                employeePositionError = validatedEmployeePosition.errorMessage,
            )

            return
        }else {
            viewModelScope.launch {
                if(employeeId.isNullOrEmpty()){
                    val result = employeeUseCases.createNewEmployee(
                        Employee(
                            employeeName = addEditState.employeeName,
                            employeePhone = addEditState.employeePhone,
                            employeeSalary = addEditState.employeeSalary,
                            employeeSalaryType = addEditState.employeeSalaryType,
                            employeePosition = addEditState.employeePosition,
                            employeeType = addEditState.employeeType,
                            employeeJoinedDate = addEditState.employeeJoinedDate,
                        )
                    )
                    when(result){
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess(result.message ?: "Employee created successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to create new employee"))
                        }
                    }

                }else {

                    val result = employeeUseCases.updateEmployee(
                        Employee(
                            employeeName = addEditState.employeeName,
                            employeePhone = addEditState.employeePhone,
                            employeeSalary = addEditState.employeeSalary,
                            employeeSalaryType = addEditState.employeeSalaryType,
                            employeePosition = addEditState.employeePosition,
                            employeeType = addEditState.employeeType,
                            employeeJoinedDate = addEditState.employeeJoinedDate,
                        ),
                        employeeId
                    )
                    when(result){
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError( "Unable to Update Employee"))
                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Employee updated successfully"))
                        }
                    }
                }
            }
            addEditState = AddEditEmployeeState()
        }
    }

    private fun getEmployeeById(employeeId: String) {
        if(employeeId.isNotEmpty()) {
            viewModelScope.launch {
                when(val result = employeeUseCases.getEmployeeById(employeeId)) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        result.data?.let {
                            addEditState = addEditState.copy(
                                employeeName = it.employeeName,
                                employeePhone = it.employeePhone,
                                employeeSalary = it.employeeSalary,
                                employeeSalaryType = it.employeeSalaryType,
                                employeePosition = it.employeePosition,
                                employeeType = it.employeeType,
                                employeeJoinedDate = it.employeeJoinedDate
                            )
                        }
                    }
                    is Resource.Error -> {}
                }
            }
        }
    }

}