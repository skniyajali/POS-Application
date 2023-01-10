package com.niyaj.popos.features.employee_attendance.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository

class UpdateAbsentEntry(private val attendanceRepository: AttendanceRepository) {
    suspend operator fun invoke(attendanceId: String, attendance: EmployeeAttendance): Resource<Boolean> {
        return attendanceRepository.updateAbsentEntry(attendanceId, attendance)
    }
}