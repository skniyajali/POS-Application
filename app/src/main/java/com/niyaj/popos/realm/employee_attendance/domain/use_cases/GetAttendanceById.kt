package com.niyaj.popos.realm.employee_attendance.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.realm.employee_attendance.domain.repository.AttendanceRepository

class GetAttendanceById(private val attendanceRepository: AttendanceRepository) {

    operator fun invoke(attendanceId: String): Resource<EmployeeAttendance?> {
        return attendanceRepository.getAttendanceById(attendanceId)
    }
}