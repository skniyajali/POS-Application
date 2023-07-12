package com.niyaj.popos.features.reminder.presentation.daily_salary_reminder

import android.app.Application
import android.text.format.DateUtils
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.employee.domain.util.PaymentType
import com.niyaj.popos.features.employee.domain.util.SalaryType
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.features.reminder.domain.model.DailySalaryReminder
import com.niyaj.popos.features.reminder.domain.model.EmployeeReminderWithStatusState
import com.niyaj.popos.features.reminder.domain.model.toReminder
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import com.niyaj.popos.features.reminder.domain.util.PaymentStatus
import com.niyaj.popos.features.reminder.domain.util.ReminderType
import com.niyaj.popos.utils.getStartTime
import com.niyaj.popos.utils.stopPendingIntentNotification
import com.niyaj.popos.utils.toDailySalaryAmount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class DailySalaryReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val salaryRepository : SalaryRepository,
    private val application : Application
): ViewModel() {

    private val _employees = MutableStateFlow(EmployeeReminderWithStatusState())
    val employees = _employees.asStateFlow()

    private val _selectedEmployees = mutableStateListOf<String>()
    val selectedEmployees: SnapshotStateList<String> = _selectedEmployees

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _selectedDate = MutableStateFlow(getStartTime)
    val selectedDate = _selectedDate.asStateFlow()

    private var count: Int = 0

    init {
        getDailySalaryReminderEmployee()
    }

    /**
     *
     */
    fun onEvent(event: DailySalaryReminderEvent) {
        when(event) {

            is DailySalaryReminderEvent.SelectEmployee -> {
                if (_selectedEmployees.contains(event.employeeId)) {
                    _selectedEmployees.remove(event.employeeId)
                }else {
                    _selectedEmployees.add(event.employeeId)
                }
            }

            is DailySalaryReminderEvent.SelectAllEmployee -> {
                count += 1

                val employees = _employees.value.employees.filter { it.paymentStatus == PaymentStatus.NotPaid }

                if (employees.isNotEmpty()){
                    employees.forEach { employeeList ->
                        if (count % 2 != 0){
                            val selectedEmployee = _selectedEmployees.find { it == employeeList.employee.employeeId }

                            if (selectedEmployee == null){
                                _selectedEmployees.add(employeeList.employee.employeeId)
                            }
                        }else {
                            _selectedEmployees.remove(employeeList.employee.employeeId)
                        }
                    }
                }
            }

            is DailySalaryReminderEvent.SelectDate -> {
                viewModelScope.launch {
                    _selectedDate.emit(event.date)

                    getDailySalaryReminderEmployee(event.date)
                }
            }

            is DailySalaryReminderEvent.MarkAsPaid -> {
                viewModelScope.launch {
                    _selectedEmployees.forEach { employeeId ->
                        val employeeWithStatus = _employees.value.employees.find { it.employee.employeeId == employeeId }
                        if (employeeWithStatus!= null) {
                            val result = salaryRepository.addNewSalary(
                                EmployeeSalary(
                                    employee = employeeWithStatus.employee,
                                    salaryType = SalaryType.Salary.salaryType,
                                    employeeSalary = employeeWithStatus.employee.employeeSalary.toDailySalaryAmount(),
                                    salaryGivenDate = _selectedDate.value.ifEmpty { getStartTime },
                                    salaryPaymentType = PaymentType.Cash.paymentType,
                                    salaryNote = "Created from reminder"
                                )
                            )

                            when(result) {
                                is Resource.Loading -> {
                                    _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                                }
                                is Resource.Success -> {
                                    _eventFlow.emit(UiEvent.Success("${employeeWithStatus.employee.employeeName} Marked as paid."))
                                }
                                is Resource.Error -> {
                                    _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to mark ${employeeWithStatus.employee.employeeName} as paid."))
                                }
                            }
                        }
                    }

                    val markAsCompleted = DateUtils.isToday(_selectedDate.value.toLong())

                    val result = reminderRepository.createOrUpdateReminder(DailySalaryReminder(isCompleted = markAsCompleted).toReminder())

                    if (result) {
                        _eventFlow.emit(UiEvent.Success("Selected employee marked as paid on selected Date."))
                        val reminder = reminderRepository.getDailySalaryReminder()!!
                        stopPendingIntentNotification(application.applicationContext, reminder.notificationId)
                    }
                }
            }
        }
    }

    private fun getDailySalaryReminderEmployee(salaryDate: String = _selectedDate.value) {
        viewModelScope.launch {
            reminderRepository.getReminderEmployee(salaryDate, ReminderType.DailySalary).collectLatest{ result ->
                when(result){
                    is Resource.Loading -> {
                        _employees.value = _employees.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _employees.value = _employees.value.copy(
                                employees = it,
                            )
                        }
                    }
                    is Resource.Error -> {
                        _employees.value = _employees.value.copy(error = result.message)
                    }
                }
            }
        }
    }

}