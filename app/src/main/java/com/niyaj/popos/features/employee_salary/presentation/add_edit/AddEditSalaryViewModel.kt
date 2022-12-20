package com.niyaj.popos.features.employee_salary.presentation.add_edit

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
import com.niyaj.popos.features.employee.domain.util.PaymentType
import com.niyaj.popos.features.employee.presentation.EmployeeState
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.employee_salary.domain.use_cases.SalaryUseCases
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateEmployee
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateGiveDate
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidatePaymentType
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateSalary
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateSalaryNote
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateSalaryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditSalaryViewModel @Inject constructor(
    private val validateEmployee: ValidateEmployee,
    private val validateGiveDate: ValidateGiveDate,
    private val validateSalary: ValidateSalary,
    private val validateSalaryType: ValidateSalaryType,
    private val validatePaymentType: ValidatePaymentType,
    private val validateSalaryNote: ValidateSalaryNote,
    private val salaryUseCases: SalaryUseCases,
    private val employeeUseCases: EmployeeUseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var employeeState by mutableStateOf(EmployeeState())

    var addEditSalaryState by mutableStateOf(AddEditSalaryState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    init {
        savedStateHandle.get<String>("employeeId")?.let { employeeId ->
            getEmployeeById(employeeId)
        }

        savedStateHandle.get<String>("salaryId")?.let { salaryId ->
            getSalaryById(salaryId)
        }

        getAllEmployees(filterEmployee = FilterEmployee.ByEmployeeId(SortType.Descending))
    }

    fun onEvent(event: AddEditSalaryEvent) {
        when (event) {
            is AddEditSalaryEvent.EmployeeChanged -> {
                getEmployeeById(event.employeeId)
            }

            is AddEditSalaryEvent.SalaryChanged -> {
                addEditSalaryState = addEditSalaryState.copy(
                    salary = event.salary
                )
            }

            is AddEditSalaryEvent.SalaryDateChanged -> {
                addEditSalaryState = addEditSalaryState.copy(
                    salaryDate = event.salaryDate
                )
            }

            is AddEditSalaryEvent.SalaryTypeChanged -> {
                addEditSalaryState = addEditSalaryState.copy(
                    salaryType = event.salaryType
                )
            }

            is AddEditSalaryEvent.PaymentTypeChanged -> {
                addEditSalaryState = addEditSalaryState.copy(
                    salaryPaymentType = event.paymentType
                )
            }

            is AddEditSalaryEvent.SalaryNoteChanged -> {
                addEditSalaryState = addEditSalaryState.copy(
                    salaryNote = event.salaryNote
                )
            }

            is AddEditSalaryEvent.UpdateSalaryEntry -> {
                addEditSalaryEntry(event.salaryId)
            }

            is AddEditSalaryEvent.AddSalaryEntry -> {
                addEditSalaryEntry()
            }

        }
    }

    private fun addEditSalaryEntry(salaryId: String? = null) {

        val validateEmployee = validateEmployee.validate(addEditSalaryState.employee.employeeId)
        val validateSalary = validateSalary.validate(addEditSalaryState.salary)
        val validateSalaryType = validateSalaryType.validate(addEditSalaryState.salaryType)
        val validateGiveDate = validateGiveDate.validate(addEditSalaryState.salaryDate)
        val validatePaymentType = validatePaymentType.validate(addEditSalaryState.salaryPaymentType)
        val validateSalaryNote = validateSalaryNote.validate(
            salaryNote = addEditSalaryState.salaryNote,
            isRequired = addEditSalaryState.salaryPaymentType == PaymentType.Both.paymentType
        )

        val validationResult = listOf(
            validateEmployee,
            validateSalary,
            validateSalaryType,
            validateGiveDate,
            validatePaymentType,
            validateSalaryNote
        ).any {
            !it.successful
        }

        if (validationResult) {
            addEditSalaryState = addEditSalaryState.copy(
                employeeError = validateEmployee.errorMessage,
                salaryError = validateSalary.errorMessage,
                salaryTypeError = validateSalaryType.errorMessage,
                salaryDateError = validateGiveDate.errorMessage,
                salaryPaymentTypeError = validatePaymentType.errorMessage,
                salaryNoteError = validateSalaryNote.errorMessage
            )

            return
        } else {
            viewModelScope.launch {
                if (salaryId.isNullOrEmpty()){
                    val result = salaryUseCases.addNewSalary(
                        EmployeeSalary(
                            employee = addEditSalaryState.employee,
                            salaryType = addEditSalaryState.salaryType,
                            employeeSalary = addEditSalaryState.salary,
                            salaryGivenDate = addEditSalaryState.salaryDate,
                            salaryPaymentType = addEditSalaryState.salaryPaymentType,
                            salaryNote = addEditSalaryState.salaryNote
                        )
                    )

                    when(result){
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Salary has been added"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to add salary entry"))
                        }
                    }
                }else {
                    val result = salaryUseCases.updateSalary(
                        salaryId,
                        EmployeeSalary(
                            employee = addEditSalaryState.employee,
                            salaryType = addEditSalaryState.salaryType,
                            employeeSalary = addEditSalaryState.salary,
                            salaryGivenDate = addEditSalaryState.salaryDate,
                            salaryPaymentType = addEditSalaryState.salaryPaymentType,
                            salaryNote = addEditSalaryState.salaryNote
                        )
                    )

                    when(result){
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Salary has been updated successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to update salary entry"))
                        }
                    }
                }
            }

            addEditSalaryState = AddEditSalaryState()

        }
    }

    private fun getAllEmployees(filterEmployee: FilterEmployee, searchText: String = "") {
        viewModelScope.launch {
            employeeUseCases.getAllEmployee(filterEmployee, searchText).collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        employeeState = employeeState.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            employeeState = employeeState.copy(
                                employees = it,
                                filterEmployee = filterEmployee
                            )
                        }
                    }
                    is Resource.Error -> {
                        employeeState = employeeState.copy(error = "Unable to load resources")
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to load resources"))
                    }
                }
            }
        }
    }

    private fun getEmployeeById(employeeId: String) {
        if(employeeId.isNotEmpty()) {
            viewModelScope.launch {
                when(val result = employeeUseCases.getEmployeeById(employeeId)) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        result.data?.let {
                            addEditSalaryState = addEditSalaryState.copy(
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

    private fun getSalaryById(salaryId: String) {
        when(val result = salaryUseCases.getSalaryById(salaryId)) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                result.data?.let { salary ->
                    if (salary.employee != null){
                        addEditSalaryState = addEditSalaryState.copy(
                            employee = salary.employee!!,
                            salary = salary.employeeSalary,
                            salaryType = salary.salaryType,
                            salaryDate = salary.salaryGivenDate,
                            salaryPaymentType = salary.salaryPaymentType,
                            salaryNote = salary.salaryNote
                        )
                    }
                }
            }
            is Resource.Error -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to get salary information"))
                }
            }
        }
    }
}