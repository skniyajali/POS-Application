package com.niyaj.popos.features.employee_attendance.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceValidationRepository
import javax.inject.Inject

class ValidateIsAbsent @Inject constructor(
    private val attendanceValidationRepository: AttendanceValidationRepository
) {

    operator fun invoke(isAbsent: Boolean): ValidationResult {
        return attendanceValidationRepository.validateIsAbsent(isAbsent)
    }
}