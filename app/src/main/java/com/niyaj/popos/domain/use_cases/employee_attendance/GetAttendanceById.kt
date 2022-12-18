package com.niyaj.popos.domain.use_cases.employee_attendance

import com.niyaj.popos.domain.model.EmployeeAttendance
import com.niyaj.popos.domain.repository.AttendanceRepository
import com.niyaj.popos.domain.util.Resource

class GetAttendanceById(private val attendanceRepository: AttendanceRepository) {

    operator fun invoke(attendanceId: String): Resource<EmployeeAttendance?> {
        return attendanceRepository.getAttendanceById(attendanceId)
    }
}