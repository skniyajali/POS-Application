package com.niyaj.popos.features.employee_attendance.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceValidationRepository
import javax.inject.Inject

class ValidateAbsentDate @Inject constructor(
    private val attendanceValidationRepository: AttendanceValidationRepository
) {
    operator fun invoke(absentDate: String, employeeId: String? = null, attendanceId: String? = null): ValidationResult {
        return attendanceValidationRepository.validateAbsentDate(absentDate, employeeId, attendanceId)
    }
}