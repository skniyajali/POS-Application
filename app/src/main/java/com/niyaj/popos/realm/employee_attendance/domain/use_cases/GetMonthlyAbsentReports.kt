package com.niyaj.popos.realm.employee_attendance.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee_attendance.domain.repository.AttendanceRepository
import com.niyaj.popos.realm.employee_attendance.domain.util.AbsentReport
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyAbsentReports @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) {

    operator fun invoke(employeeId: String): Flow<Resource<List<AbsentReport>>> {
        return attendanceRepository.getMonthlyAbsentReport(employeeId)
    }
}