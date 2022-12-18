package com.niyaj.popos.domain.use_cases.employee_attendance

import com.niyaj.popos.domain.model.AbsentReport
import com.niyaj.popos.domain.repository.AttendanceRepository
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyAbsentReports @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) {

    operator fun invoke(employeeId: String): Flow<Resource<List<AbsentReport>>> {
        return attendanceRepository.getMonthlyAbsentReport(employeeId)
    }
}