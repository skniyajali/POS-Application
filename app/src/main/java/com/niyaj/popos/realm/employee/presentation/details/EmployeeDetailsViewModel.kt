package com.niyaj.popos.realm.employee.presentation.details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.realm.employee.domain.use_cases.EmployeeUseCases
import com.niyaj.popos.realm.employee_attendance.domain.use_cases.AttendanceUseCases
import com.niyaj.popos.realm.employee_salary.domain.use_cases.SalaryUseCases
import com.niyaj.popos.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EmployeeDetailsViewModel @Inject constructor(
    private val employeeUseCases: EmployeeUseCases,
    private val salaryUseCases: SalaryUseCases,
    private val attendanceUseCases: AttendanceUseCases,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _employeeDetails = mutableStateOf(EmployeeDetailsState())
    val employeeDetails: State<EmployeeDetailsState> = _employeeDetails

    private val _paymentDetails = mutableStateOf(EmployeePaymentState())
    val paymentDetails: State<EmployeePaymentState> = _paymentDetails

    private val _salaries = mutableStateOf(EmployeeSalaryState())
    val salaries: State<EmployeeSalaryState> = _salaries

    private val _salaryDates = mutableStateOf(EmployeeSalaryDateState())
    val salaryDates: State<EmployeeSalaryDateState> = _salaryDates

    private val _selectedSalaryDate = mutableStateOf<Pair<String, String>?>(null)
    val selectedSalaryDate: State<Pair<String, String>?> = _selectedSalaryDate

    private val _absentReports = mutableStateOf(MonthlyAbsentReportState())
    val absentReports: State<MonthlyAbsentReportState> = _absentReports

    init {
        savedStateHandle.get<String>("employeeId")?.let { employeeId ->
            getEmployeeById(employeeId)
            getSalaryDetails(employeeId)
            getSalaryCalculableDate(employeeId)
            getPaymentDetails(employeeId)
            getMonthlyAbsentReport(employeeId)
        }
    }


    fun onEvent(event: EmployeeDetailsEvent) {
        when (event) {
            is EmployeeDetailsEvent.RefreshEmployeeDetails -> {
                savedStateHandle.get<String>("employeeId")?.let { employeeId ->
                    getEmployeeById(employeeId)
                    getSalaryDetails(employeeId)
                    getSalaryCalculableDate(employeeId)
                    getPaymentDetails(employeeId)
                    getMonthlyAbsentReport(employeeId)
                }
            }

            is EmployeeDetailsEvent.OnChooseSalaryDate -> {
                _selectedSalaryDate.value = event.date

                savedStateHandle.get<String>("employeeId")?.let { employeeId ->
                    getPaymentDetails(employeeId)
                }
            }
        }
    }

    private fun getEmployeeById(employeeId: String) {
        if(employeeId.isNotEmpty()) {
            viewModelScope.launch {
                when(val result = employeeUseCases.getEmployeeById(employeeId)) {
                    is Resource.Loading -> {
                        _employeeDetails.value = _employeeDetails.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _employeeDetails.value = _employeeDetails.value.copy(
                                employee = it
                            )
                        }
                    }
                    is Resource.Error -> {
                        _employeeDetails.value = _employeeDetails.value.copy(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun getPaymentDetails(employeeId: String) {
        viewModelScope.launch {
            val selectedDate =
                if (_selectedSalaryDate.value != null)
                    _selectedSalaryDate.value
                else if (_salaryDates.value.dates.isNotEmpty())
                    Pair(_salaryDates.value.dates.first().startDate, _salaryDates.value.dates.first().endDate)
                else Pair("", "")

            if (selectedDate != null) {
                when (val result = salaryUseCases.getSalaryByEmployeeId(employeeId,  selectedDate)) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        result.data?.let {
                            _paymentDetails.value = _paymentDetails.value.copy(
                                payments = it
                            )
                        }
                    }
                    is Resource.Error -> {
                        _paymentDetails.value = _paymentDetails.value.copy(error = result.message)
                    }
                }
            }
        }
    }

    private fun getSalaryDetails(employeeId: String) {
        viewModelScope.launch {
            salaryUseCases.getEmployeeSalary(employeeId).collect { result ->
                when(result) {
                    is Resource.Loading -> {
                        _salaries.value = _salaries.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _salaries.value = _salaries.value.copy(
                                payments = it
                            )
                        }

                    }
                    is Resource.Error -> {
                        _salaries.value = _salaries.value.copy(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun getSalaryCalculableDate(employeeId: String) {
        val result = salaryUseCases.getSalaryCalculableDate(employeeId)

        result.data?.let {
            _salaryDates.value = _salaryDates.value.copy(
                dates = it
            )
        }
    }

    private fun getMonthlyAbsentReport(employeeId: String) {
        viewModelScope.launch {
            attendanceUseCases.getMonthlyAbsentReports(employeeId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _absentReports.value = _absentReports.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _absentReports.value = _absentReports.value.copy(
                                absents = it
                            )
                        }
                    }
                    is Resource.Error -> {
                        _absentReports.value = _absentReports.value.copy(
                           error = result.message
                        )
                    }
                }
            }
        }
    }

}