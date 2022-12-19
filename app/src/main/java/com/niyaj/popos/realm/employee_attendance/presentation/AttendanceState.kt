package com.niyaj.popos.realm.employee_attendance.presentation

import com.niyaj.popos.realm.employee_attendance.domain.model.EmployeeAttendance

data class AttendanceState(
    val attendances: List<EmployeeAttendance> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
