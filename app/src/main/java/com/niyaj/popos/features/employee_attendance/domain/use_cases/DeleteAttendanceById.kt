package com.niyaj.popos.features.employee_attendance.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository

class DeleteAttendanceById(private val attendanceRepository: AttendanceRepository) {

    operator fun invoke(attendanceId: String): Resource<Boolean> {
        return attendanceRepository.removeAttendanceById(attendanceId)
    }
}