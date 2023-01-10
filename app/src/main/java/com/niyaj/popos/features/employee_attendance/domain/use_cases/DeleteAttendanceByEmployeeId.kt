package com.niyaj.popos.features.employee_attendance.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository

class DeleteAttendanceByEmployeeId(private val attendanceRepository: AttendanceRepository) {
    suspend operator fun invoke(employeeId: String, date: String): Resource<Boolean> {
        return attendanceRepository.removeAttendanceByEmployeeId(employeeId, date)
    }
}