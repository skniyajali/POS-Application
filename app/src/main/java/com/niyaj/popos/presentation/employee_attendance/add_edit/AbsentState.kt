package com.niyaj.popos.presentation.employee_attendance.add_edit

import com.niyaj.popos.domain.model.Employee
import com.niyaj.popos.util.toMilliSecond
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
