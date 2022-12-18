package com.niyaj.popos.presentation.employee_attendance

import com.niyaj.popos.domain.model.EmployeeAttendance

data class AttendanceState(
    val attendances: List<EmployeeAttendance> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
