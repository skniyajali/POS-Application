package com.niyaj.popos.features.employee_attendance.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateAbsentEmployee @Inject constructor() {

    fun validate(employeeId: String): ValidationResult {

        if (employeeId.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must not be empty",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }
}