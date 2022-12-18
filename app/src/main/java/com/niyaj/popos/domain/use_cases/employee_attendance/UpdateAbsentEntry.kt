package com.niyaj.popos.domain.use_cases.employee_attendance

import com.niyaj.popos.domain.model.EmployeeAttendance
import com.niyaj.popos.domain.repository.AttendanceRepository
import com.niyaj.popos.domain.util.Resource

class UpdateAbsentEntry(private val attendanceRepository: AttendanceRepository) {

    operator fun invoke(attendanceId: String, attendance: EmployeeAttendance): Resource<Boolean> {
        return attendanceRepository.updateAbsentEntry(attendanceId, attendance)
    }
}