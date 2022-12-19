package com.niyaj.popos.realm.employee_attendance.domain.use_cases.validation

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