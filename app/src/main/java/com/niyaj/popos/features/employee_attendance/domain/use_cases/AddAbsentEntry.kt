package com.niyaj.popos.features.employee_attendance.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository

class AddAbsentEntry(private val attendanceRepository: AttendanceRepository) {

    suspend operator fun invoke(attendance: EmployeeAttendance): Resource<Boolean> {
        return attendanceRepository.addAbsentEntry(attendance)
    }
}