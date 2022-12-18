package com.niyaj.popos.presentation.employee_salary.add_edit

import com.niyaj.popos.domain.model.Employee
import com.niyaj.popos.domain.util.PaymentType
import com.niyaj.popos.domain.util.SalaryType
import com.niyaj.popos.util.toMilliSecond
import java.time.LocalDate

data class AddEditSalaryState(
    val employee: Employee = Employee(),
    val employeeError: String? = null,

    val salary: String = "",
    val salaryError: String? = null,

    val salaryType: String = SalaryType.Advanced.salaryType,
    val salaryTypeError: String? = null,

    val salaryDate: String = LocalDate.now().toMilliSecond,
    val salaryDateError: String? = null,

    val salaryPaymentType: String = PaymentType.Cash.paymentType,
    val salaryPaymentTypeError: String? = null,

    val salaryNote: String = "",
    val salaryNoteError: String? = null,
)
