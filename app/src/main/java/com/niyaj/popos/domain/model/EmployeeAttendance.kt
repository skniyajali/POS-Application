package com.niyaj.popos.domain.model

import com.niyaj.popos.realm.employee.domain.model.Employee
import com.niyaj.popos.realm.employee_attendance.AttendanceRealm

data class EmployeeAttendance(
    val attendeeId: String = "",
    val employee: Employee = Employee(),
    val isAbsent: Boolean = false,
    val absentReason: String = "",
    val absentDate: String = "",
    val createdAt: String = "",
    val updatedAt: String? = null,
)

data class AbsentReport(
    val startDate: String = "",
    val endDate: String = "",
    val absent: List<EmployeeAttendance> = emptyList(),
)


data class AbsentReportRealm(
    val startDate: String = "",
    val endDate: String = "",
    val absent: List<AttendanceRealm> = emptyList(),
)