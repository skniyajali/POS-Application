package com.niyaj.popos.features.employee_attendance.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentReport
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyAbsentReports @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) {
    suspend operator fun invoke(employeeId: String): Flow<Resource<List<AbsentReport>>> {
        return attendanceRepository.getMonthlyAbsentReport(employeeId)
    }
}