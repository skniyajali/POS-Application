package com.niyaj.popos.domain.use_cases.employee_attendance.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateIsAbsent @Inject constructor() {

    fun validate(isAbsent: Boolean): ValidationResult {

        if (!isAbsent) {
            return ValidationResult(
                successful = false,
                errorMessage = "Employee must be absent."
            )
        }

        return ValidationResult(true)
    }
}