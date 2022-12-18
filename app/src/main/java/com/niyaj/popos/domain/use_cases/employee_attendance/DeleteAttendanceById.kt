package com.niyaj.popos.domain.use_cases.employee_attendance

import com.niyaj.popos.domain.repository.AttendanceRepository
import com.niyaj.popos.domain.util.Resource

class DeleteAttendanceById(private val attendanceRepository: AttendanceRepository) {

    operator fun invoke(attendanceId: String): Resource<Boolean> {
        return attendanceRepository.removeAttendanceById(attendanceId)
    }
}