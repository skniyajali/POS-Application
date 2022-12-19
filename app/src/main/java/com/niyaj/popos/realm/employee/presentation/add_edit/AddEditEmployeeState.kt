package com.niyaj.popos.realm.employee.presentation.add_edit

import com.niyaj.popos.domain.util.EmployeeSalaryType
import com.niyaj.popos.domain.util.EmployeeType
import com.niyaj.popos.util.toMilliSecond
import java.time.LocalDate

data class AddEditEmployeeState(
    val employeeName: String = "",
    val employeeNameError: String? = null,
    val employeePhone: String = "",
    val employeePhoneError: String? = null,
    val employeeSalary: String = "",
    val employeeSalaryError: String? = null,
    val employeeSalaryType: String = EmployeeSalaryType.Monthly.salaryType,
    val employeePosition: String = "",
    val employeePositionError: String? = null,
    val employeeType: String = EmployeeType.FullTime.employeeType,
    val employeeJoinedDate: String = LocalDate.now().toMilliSecond,
)

val positions = listOf(
    "Master",
    "Assistant",
    "Captain",
    "Manager",
    "Cleaner",
    "Senior Cook",
    "Junior Cook",
    "Chef"
)