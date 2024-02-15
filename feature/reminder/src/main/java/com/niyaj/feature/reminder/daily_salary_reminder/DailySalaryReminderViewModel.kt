package com.niyaj.feature.reminder.daily_salary_reminder

import android.text.format.DateUtils
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.getStartTime
import com.niyaj.common.utils.toDailySalaryAmount
import com.niyaj.data.mapper.toReminder
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.data.repository.ReminderRepository
import com.niyaj.model.DailySalaryReminder
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentStatus
import com.niyaj.model.PaymentType
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
class DailySalaryReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val salaryRepository: PaymentRepository,
    private val notifier: Notifier,
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(getStartTime)
    val selectedDate = _selectedDate.asStateFlow()

    val employees = _selectedDate.flatMapLatest {
        reminderRepository.getReminderEmployee(it, ReminderType.DailySalary)
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

    fun onEvent(event: DailySalaryReminderEvent) {
        when (event) {

            is DailySalaryReminderEvent.SelectEmployee -> {
                if (_selectedEmployees.contains(event.employeeId)) {
                    _selectedEmployees.remove(event.employeeId)
                } else {
                    _selectedEmployees.add(event.employeeId)
                }
            }

            is DailySalaryReminderEvent.SelectAllEmployee -> {
                count += 1

                val employees = employees.value.filter { it.paymentStatus == PaymentStatus.NotPaid }

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

            is DailySalaryReminderEvent.SelectDate -> {
                _selectedDate.value = event.date
            }

            is DailySalaryReminderEvent.MarkAsPaid -> {
                viewModelScope.launch {
                    _selectedEmployees.forEach { employeeId ->
                        val employeeWithStatus =
                            employees.value.find { it.employee.employeeId == employeeId }
                        if (employeeWithStatus != null) {
                            val result = salaryRepository.addNewPayment(
                                Payment(
                                    employee = employeeWithStatus.employee,
                                    paymentMode = PaymentMode.Cash,
                                    paymentAmount = employeeWithStatus.employee.employeeSalary.toDailySalaryAmount(),
                                    paymentDate = _selectedDate.value.ifEmpty { getStartTime },
                                    paymentType = PaymentType.Salary,
                                    paymentNote = "Created from reminder"
                                )
                            )

                            when (result) {
                                is Resource.Success -> {
                                    _eventFlow.emit(UiEvent.Success("${employeeWithStatus.employee.employeeName} Marked as paid."))
                                }

                                is Resource.Error -> {
                                    _eventFlow.emit(
                                        UiEvent.Error(
                                            result.message
                                                ?: "Unable to mark ${employeeWithStatus.employee.employeeName} as paid."
                                        )
                                    )
                                }
                            }
                        }
                    }

                    val markAsCompleted = DateUtils.isToday(_selectedDate.value.toLong())
                    val reminder = DailySalaryReminder(isCompleted = markAsCompleted).toReminder()

                    val result = reminderRepository.createOrUpdateReminder(reminder)

                    if (result) {
                        _eventFlow.emit(UiEvent.Success("Selected employee marked as paid on selected Date."))
                        notifier.stopDailySalaryNotification(reminder.notificationId)
                    }
                }
            }
        }
    }

}