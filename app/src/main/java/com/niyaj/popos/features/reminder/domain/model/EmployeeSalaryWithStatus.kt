package com.niyaj.popos.features.reminder.domain.model

import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.reminder.domain.util.PaymentStatus
import com.niyaj.popos.features.reminder.domain.util.ReminderType

data class EmployeeSalaryWithStatus(
    val employee: Employee = Employee(),
    val paymentStatus: PaymentStatus = PaymentStatus.NotPaid,
)

data class EmployeeReminderWithStatus(
    val employee : Employee = Employee(),
    val paymentStatus : PaymentStatus = PaymentStatus.NotPaid,
    val absentStatus: Boolean = false,
    val reminderType: ReminderType = ReminderType.Attendance,
    val employeeSalary: String = "0",
)


data class EmployeeReminderWithStatusState(
    val employees: List<EmployeeReminderWithStatus> = emptyList(),
    val isLoading : Boolean = false,
    val error : String? = null
)