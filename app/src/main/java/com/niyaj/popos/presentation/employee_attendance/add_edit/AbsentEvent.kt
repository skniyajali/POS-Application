package com.niyaj.popos.presentation.employee_attendance.add_edit

sealed class AbsentEvent {

    data class EmployeeChanged(val employeeId: String): AbsentEvent()

    data class AbsentChanged(val isAbsent: Boolean): AbsentEvent()

    data class AbsentDateChanged(val absentDate: String): AbsentEvent()

    data class AbsentReasonChanged(val absentReason: String): AbsentEvent()

    object AddAbsentEntry: AbsentEvent()

    data class UpdateAbsentEntry(val attendanceId: String): AbsentEvent()
}
