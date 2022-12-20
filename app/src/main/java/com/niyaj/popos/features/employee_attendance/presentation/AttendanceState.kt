package com.niyaj.popos.features.employee_attendance.presentation

import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance

data class AttendanceState(
    val attendances: List<EmployeeAttendance> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
