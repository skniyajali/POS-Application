package com.niyaj.popos.domain.use_cases.employee_attendance

import com.niyaj.popos.domain.repository.AttendanceRepository
import com.niyaj.popos.domain.util.Resource

class DeleteAttendanceByEmployeeId(private val attendanceRepository: AttendanceRepository) {

    operator fun invoke(employeeId: String, date: String): Resource<Boolean> {
        return attendanceRepository.removeAttendanceByEmployeeId(employeeId, date)
    }
}