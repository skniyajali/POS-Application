package com.niyaj.popos.presentation.employee_attendance

sealed class AttendanceEvent {

    data class SelectAttendance(val attendanceId: String) : AttendanceEvent()

    data class SelectEmployee(val employeeId: String) : AttendanceEvent()

    data class DeleteAttendance(val attendanceId: String): AttendanceEvent()

    data class OnSearchAttendance(val searchText: String): AttendanceEvent()

    object ToggleSearchBar : AttendanceEvent()

    object RefreshAttendance : AttendanceEvent()
}
