package com.niyaj.popos.realm.employee_attendance.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.realm.employee_attendance.domain.repository.AttendanceRepository

class AddAbsentEntry(private val attendanceRepository: AttendanceRepository) {

    operator fun invoke(attendance: EmployeeAttendance): Resource<Boolean> {
        return attendanceRepository.addAbsentEntry(attendance)
    }
}