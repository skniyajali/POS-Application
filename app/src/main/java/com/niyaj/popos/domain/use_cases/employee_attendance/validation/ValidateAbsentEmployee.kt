package com.niyaj.popos.domain.use_cases.employee_attendance.validation

import com.niyaj.popos.domain.util.ValidationResult
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