package com.niyaj.popos.realm.employee_attendance.domain.use_cases

import com.niyaj.popos.realm.employee_attendance.domain.repository.AttendanceRepository

class FindAttendanceByAbsentDate(private val attendanceRepository: AttendanceRepository) {

    operator fun invoke(absentDate: String, employeeId: String, attendanceId: String? = null): Boolean {
        return attendanceRepository.findAttendanceByAbsentDate(absentDate, employeeId, attendanceId)
    }
}