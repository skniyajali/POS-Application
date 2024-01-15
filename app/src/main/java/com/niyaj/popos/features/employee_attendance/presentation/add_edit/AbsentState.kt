package com.niyaj.popos.features.employee_attendance.presentation.add_edit

import com.niyaj.popos.common.utils.toMilliSecond
import com.niyaj.popos.features.employee.domain.model.Employee
import java.time.LocalDate

data class AbsentState(
    val employee: Employee = Employee(),
    val employeeError: String? = null,

    val isAbsent: Boolean = true,
    val isAbsentError: String? = null,

    val absentDate: String = LocalDate.now().toMilliSecond,
    val absentDateError: String? = null,

    val absentReason: String = "",
)
