package com.niyaj.popos.domain.use_cases.employee_attendance

import com.niyaj.popos.domain.repository.AttendanceRepository

class FindAttendanceByAbsentDate(private val attendanceRepository: AttendanceRepository) {

    operator fun invoke(absentDate: String, employeeId: String, attendanceId: String? = null): Boolean {
        return attendanceRepository.findAttendanceByAbsentDate(absentDate, employeeId, attendanceId)
    }
}